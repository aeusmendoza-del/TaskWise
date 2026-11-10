package taskwise;

import com.example.taskwise.R;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;

public class taskwise_resources extends AppCompatActivity {
    private static final String PREFS = "taskwise_resources";
    private static final String HISTORY = "search_history";
    private EditText searchInput;
    private LinearLayout recentContainer;
    private LinearLayout subjectChips;
    private TextView emptyHistory;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskwise_resources);
        searchInput = findViewById(R.id.resourceSearchInput);
        recentContainer = findViewById(R.id.resourceRecentSearches);
        subjectChips = findViewById(R.id.resourceSubjectChips);
        emptyHistory = findViewById(R.id.resourceEmptyHistory);
        findViewById(R.id.resourcesBackButton).setOnClickListener(v -> finish());
        findViewById(R.id.resourceSearchGoogleButton).setOnClickListener(v -> search(searchInput.getText().toString(), "google"));
        findViewById(R.id.resourceSearchYoutubeButton).setOnClickListener(v -> search(searchInput.getText().toString(), "youtube"));
        findViewById(R.id.resourceSearchWikipediaButton).setOnClickListener(v -> search(searchInput.getText().toString(), "wikipedia"));
        findViewById(R.id.clearResourceHistoryButton).setOnClickListener(v -> clearHistory());
        searchInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search(searchInput.getText().toString(), "google");
                return true;
            }
            return false;
        });
        renderSubjectChips();
        renderHistory();
        bindNavigation();
    }

    private void search(String rawQuery, String provider) {
        String query = rawQuery == null ? "" : rawQuery.trim();
        if (query.isEmpty()) {
            searchInput.setError("Type a topic or question first");
            searchInput.requestFocus();
            return;
        }
        saveQuery(query);
        String base;
        String parameter;
        if ("youtube".equals(provider)) {
            base = "https://www.youtube.com/results";
            parameter = "search_query";
        } else if ("wikipedia".equals(provider)) {
            base = "https://en.wikipedia.org/w/index.php";
            parameter = "search";
        } else {
            base = "https://www.google.com/search";
            parameter = "q";
        }
        Uri uri = Uri.parse(base).buildUpon().appendQueryParameter(parameter, query).build();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(this, "No browser is available on this device.", Toast.LENGTH_LONG).show();
        }
    }

    private void saveQuery(String query) {
        ArrayList<String> history = getHistory();
        for (int i = history.size() - 1; i >= 0; i--)
            if (history.get(i).equalsIgnoreCase(query)) history.remove(i);
        history.add(0, query);
        while (history.size() > 6) history.remove(history.size() - 1);
        JSONArray values = new JSONArray();
        for (String item : history) values.put(item);
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(HISTORY, values.toString()).apply();
        renderHistory();
    }

    private ArrayList<String> getHistory() {
        ArrayList<String> result = new ArrayList<>();
        try {
            JSONArray values = new JSONArray(getSharedPreferences(PREFS, MODE_PRIVATE).getString(HISTORY, "[]"));
            for (int i = 0; i < values.length(); i++) {
                String value = values.optString(i, "").trim();
                if (!value.isEmpty()) result.add(value);
            }
        } catch (Exception ignored) { }
        return result;
    }

    private void renderHistory() {
        recentContainer.removeAllViews();
        ArrayList<String> history = getHistory();
        emptyHistory.setVisibility(history.isEmpty() ? View.VISIBLE : View.GONE);
        for (String query : history) {
            TextView item = new TextView(this);
            item.setText("⌕  " + query + "   ›");
            item.setTextColor(0xFF31485A);
            item.setTextSize(13);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(dp(14), 0, dp(14), 0);
            item.setBackgroundResource(R.drawable.bg_task_card);
            item.setSingleLine(true);
            item.setEllipsize(TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(46));
            params.topMargin = dp(6);
            recentContainer.addView(item, params);
            item.setOnClickListener(v -> {
                searchInput.setText(query);
                search(query, "google");
            });
        }
    }

    private void renderSubjectChips() {
        subjectChips.removeAllViews();
        ArrayList<String> subjects = readSubjects();
        if (subjects.isEmpty()) subjects.addAll(Arrays.asList(
                "Computer Programming", "Effective Communication", "General Science", "General Mathematics"));
        for (String subject : subjects) {
            TextView chip = new TextView(this);
            chip.setText(subject);
            chip.setTextSize(11);
            chip.setTextColor(0xFF536477);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(dp(14), 0, dp(14), 0);
            chip.setBackgroundResource(R.drawable.bg_tab_unselected);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(36));
            params.setMargins(0, 0, dp(8), 0);
            subjectChips.addView(chip, params);
            chip.setOnClickListener(v -> search(subject + " study guide", "google"));
        }
    }

    private ArrayList<String> readSubjects() {
        ArrayList<String> result = new ArrayList<>();
        String saved = getSharedPreferences("taskwise_subjects", MODE_PRIVATE).getString("saved_subjects", null);
        if (saved == null) return result;
        try {
            JSONArray values = new JSONArray(saved);
            for (int i = 0; i < values.length(); i++) {
                String subject = values.optString(i, "").trim();
                if (!subject.isEmpty()) result.add(subject);
            }
        } catch (Exception ignored) { }
        return result;
    }

    private void clearHistory() {
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().remove(HISTORY).apply();
        renderHistory();
    }

    private void bindNavigation() {
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

    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density + 0.5f); }
}
