package com.example.androidappdevelopmentleveltwotodoappwithlogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ListView listViewTasks;
    private TextView tvEmptyState;
    private Button btnAddTask, btnLogout;
    private int currentUserId;
    private SimpleCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        // Retrieve logged-in user session ID
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserId = prefs.getInt("logged_in_user_id", -1);

        if (currentUserId == -1) {
            // If session is missing, return to login
            redirectToLogin();
            return;
        }

        listViewTasks = findViewById(R.id.listViewTasks);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnAddTask = findViewById(R.id.btnAddtask);
        btnLogout = findViewById(R.id.btnLogout);

        loadUserTasks();

        btnAddTask.setOnClickListener(v -> showAddTaskDialog());

        btnLogout.setOnClickListener(v -> {
            // Clear session
            prefs.edit().clear().apply();
            redirectToLogin();
        });

        // Handle item click to toggle task completion status
        listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_ID));
            int currentStatus = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_STATUS));

            int newStatus = (currentStatus == 1) ? 0 : 1;
            databaseHelper.updateTaskStatus(taskId, newStatus);
            loadUserTasks();
        });

        // Handle long click to delete task permanently
        listViewTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_ID));

            new AlertDialog.Builder(this)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        databaseHelper.deleteTask(taskId);
                        loadUserTasks();
                        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
    }

    private void loadUserTasks() {
        Cursor cursor = databaseHelper.getAllTasksForUser(currentUserId);

        if (cursor == null || cursor.getCount() == 0) {
            tvEmptyState.setVisibility(View.VISIBLE);
            listViewTasks.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            listViewTasks.setVisibility(View.VISIBLE);

            String[] fromColumns = {DatabaseHelper.COLUMN_TASK_TITLE, DatabaseHelper.COLUMN_TASK_NOTES};
            int[] toViews = {android.R.id.text1, android.R.id.text2};

            cursorAdapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    fromColumns,
                    toViews,
                    0
            );
            listViewTasks.setAdapter(cursorAdapter);
        }
    }

    private void showAddTaskDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null); // Optional custom dialog layout or build programmatically

        final EditText etTitle = new EditText(this);
        etTitle.setHint("Task Title");
        final EditText etNotes = new EditText(this);
        etNotes.setHint("Optional Notes");

        LinearLayoutDialog layout = new LinearLayoutDialog(this, etTitle, etNotes);

        new AlertDialog.Builder(this)
                .setTitle("Add New Task")
                .setView(layout)
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String notes = etNotes.getText().toString().trim();

                    if (!title.isEmpty()) {
                        databaseHelper.addTask(currentUserId, title, notes);
                        loadUserTasks();
                        Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Task title cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Helper wrapper to layout dialog fields cleanly
    private static class LinearLayoutDialog extends android.widget.LinearLayout {
        public LinearLayoutDialog(android.content.Context context, EditText t1, EditText t2) {
            super(context);
            setOrientation(VERTICAL);
            setPadding(40, 20, 40, 20);
            t1.setMinHeight(120);
            t2.setMinHeight(120);
            addView(t1);
            addView(t2);
        }
    }

    private void redirectToLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}