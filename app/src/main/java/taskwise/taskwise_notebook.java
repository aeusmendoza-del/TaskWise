package taskwise;

import com.example.taskwise.R;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import java.util.ArrayList;
public class taskwise_notebook extends AppCompatActivity {
    private final ArrayList<String> notes = new ArrayList<>();
    private LinearLayout notesContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskwise_notebook);
        notesContainer = findViewById(R.id.notesContainer);
        findViewById(R.id.notebookBackButton).setOnClickListener(v -> finish());
        findViewById(R.id.addNoteCard).setOnClickListener(v -> showNoteEditor(-1));
        findViewById(R.id.addNoteImageButton).setOnClickListener(v -> showNoteEditor(-1));
        loadNotes();
        renderNotes();
        bindBottomNavigation();
    }
    private void loadNotes() {
        String raw = getSharedPreferences("taskwise_notes", MODE_PRIVATE).getString("items", null);
        if (raw == null) {
            notes.add("Review Chapter 3 - Effective Communication");
            notes.add("Remember to finish the programming activity");
            saveNotes();
            return;
        }
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) notes.add(array.optString(i));
        } catch (Exception ignored) {
            notes.clear();
        }
    }
    private void saveNotes() {
        JSONArray array = new JSONArray();
        for (String note : notes) array.put(note);
        getSharedPreferences("taskwise_notes", MODE_PRIVATE).edit()
                .putString("items", array.toString()).apply();
    }
    private void showNoteEditor(int index) {
        EditText input = new EditText(this);
        input.setHint("Write your note here");
        input.setMinLines(3);
        input.setGravity(Gravity.TOP | Gravity.START);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        int pad = dp(14);
        input.setPadding(pad, pad, pad, pad);
        if (index >= 0) input.setText(notes.get(index));
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(index < 0 ? "New Note" : "Edit Note")
                .setView(input).setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null).create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String value = input.getText().toString().trim();
            if (value.isEmpty()) {
                input.setError("Write a note first");
                return;
            }
            if (index < 0) notes.add(0, value);
            else notes.set(index, value);
            saveNotes();
            renderNotes();
            dialog.dismiss();
        }));
        dialog.show();
    }
    private void renderNotes() {
        notesContainer.removeAllViews();
        if (notes.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No notes yet. Add a note to get started.");
            empty.setTextColor(0xFF738292);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(12), dp(20), dp(12), dp(20));
            notesContainer.addView(empty);
        }
        for (int i = 0; i < notes.size(); i++) {
            final int index = i;
            TextView row = new TextView(this);
            row.setText(notes.get(i));
            row.setTextColor(0xFF34485B);
            row.setTextSize(14);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(16), dp(10), dp(16), dp(10));
            row.setMinHeight(dp(64));
            row.setBackgroundResource(R.drawable.bg_dynamic_reminder);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.topMargin = dp(8);
            notesContainer.addView(row, lp);
            row.setOnClickListener(v -> showNoteActions(index));
        }
    }
    private void showNoteActions(int index) {
        new AlertDialog.Builder(this).setTitle("Note").setMessage(notes.get(index))
                .setPositiveButton("Edit", (d, w) -> showNoteEditor(index))
                .setNegativeButton("Delete", (d, w) -> new AlertDialog.Builder(this)
                        .setTitle("Delete note?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (d2, w2) -> {
                            notes.remove(index);
                            saveNotes();
                            renderNotes();
                        }).show())
                .setNeutralButton("Close", null).show();
    }
    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
    private void bindBottomNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> finishTo(taskwise_home.class));
        findViewById(R.id.navProfile).setOnClickListener(v -> startActivity(new Intent(this, taskwise_profile.class)));
        findViewById(R.id.navSubjects).setOnClickListener(v -> startActivity(new Intent(this, taskwise_subjects.class)));
        findViewById(R.id.navTasks).setOnClickListener(v -> startActivity(new Intent(this, taskwise_deadline.class)));
        findViewById(R.id.navMenu).setOnClickListener(v -> startActivity(new Intent(this, taskwise_reminder.class)));
    }
    private void finishTo(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}