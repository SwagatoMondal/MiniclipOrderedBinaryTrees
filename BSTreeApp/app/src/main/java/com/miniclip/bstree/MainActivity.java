package com.miniclip.bstree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miniclip.bstree.entity.BSTree;
import com.miniclip.bstree.util.TreeViewBuilder;
import com.miniclip.bstree.util.dialogs.DialogListener;
import com.miniclip.bstree.util.dialogs.DialogUtil;
import com.miniclip.bstree.util.dialogs.RemoveDialogUtil;
import com.miniclip.bstree.util.JSONParser;
import com.miniclip.bstree.view.NodeView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static final String TAG = MainActivity.class.getSimpleName();

    @Nullable
    private BSTree tree;
    private TextView empty;
    private LinearLayout container;
    private NodeView treeView;
    private RemoveDialogUtil util;
    private boolean treeViewSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        empty = findViewById(R.id.empty);
        treeView = findViewById(R.id.tree);
        container = findViewById(R.id.container);

        findViewById(R.id.create).setOnClickListener(this);
        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.remove).setOnClickListener(this);
        findViewById(R.id.state).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                tree = null;
                showTreeStatus();
                return true;
            case R.id.inorder:
                if (treeViewSelected) {
                    treeViewSelected = false;
                    showTreeStatus();
                }
                return true;
            case R.id.tree:
                if (!treeViewSelected) {
                    treeViewSelected = true;
                    showTreeStatus();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    private JSONArray getRawJSON(int id) {
        try {
            InputStream is = getResources().openRawResource(id);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }

            return new JSONArray(writer.toString());
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    /**
     * Method to create a BSTree.
     * @param parser A {@link JSONParser} object to read the values from JSONArray
     * @return A {@link  BSTree} object representing the root of the tree
     */
    public native BSTree createTree(@NonNull JSONParser parser);

    /**
     * Method to create a BSTree.
     * @param tree Root of the tree
     * @param parser A {@link JSONParser} object to read the values from JSONArray
     */
    public native void addNodes(BSTree tree, @NonNull JSONParser parser);

    /**
     * Method to remove node(s) with particular value from the tree
     * @param tree Root of the tree
     * @param value Value of the nodes to be removed
     * @return the new root
     */
    public native BSTree removeValue(BSTree tree, int value);

    /**
     * Method to remove nodes(s) with particular color from the tree
     * @param tree Root of the tree
     * @param color Color of the nodes to be removed
     * @return  the new root
     */
    public native BSTree removeColor(BSTree tree, String color);

    public native JSONArray getState(BSTree tree);

    private void showTreeStatus() {
        if (null == tree) {
            empty.setVisibility(View.VISIBLE);
            treeView.setVisibility(View.GONE);
            container.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            if (treeViewSelected) {
                container.setVisibility(View.GONE);
                treeView.setTree(tree);
                treeView.invalidate();
                treeView.setVisibility(View.VISIBLE);
            } else {
                treeView.setVisibility(View.GONE);
                container.removeAllViews();
                TreeViewBuilder.viewBuilder(this, tree, container);
                container.setVisibility(View.VISIBLE);
            }
        }
    }

    @NonNull
    private DialogListener getValueListener() {
        return new DialogListener() {

            private CharSequence[] items;

            @Override
            public CharSequence[] getItems() {
                final HashSet<Integer> values = new HashSet<>();
                BSTree.uniqueValues(tree, values);

                items = new CharSequence[values.size()];
                Iterator<Integer> iterator = values.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    items[i++] = String.valueOf(iterator.next());
                }
                return items;
            }

            @Override
            public void onSelected(int which) {
                final int valueToRemove = Integer.parseInt(items[which].toString());
                Log.d(TAG, "Value to be removed : " + valueToRemove);
                tree = removeValue(tree, valueToRemove);
                showTreeStatus();
            }
        };
    }

    @NonNull
    private DialogListener getColorListener() {
        return new DialogListener() {

            private CharSequence[] items;

            @Override
            public CharSequence[] getItems() {
                final HashSet<String> colors = new HashSet<>();
                BSTree.uniqueColors(tree, colors);

                items = new CharSequence[colors.size()];
                Iterator<String> iterator = colors.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    items[i++] = iterator.next();
                }
                return items;
            }

            @Override
            public void onSelected(int which) {
                final String colorToRemove = items[which].toString();
                Log.d(TAG, "Color to be removed : " + colorToRemove);
                tree = removeColor(tree, colorToRemove);
                showTreeStatus();
            }
        };
    }

    private DialogListener getFileChooserListener(final boolean create) {
        return new DialogListener() {
            @Override
            public CharSequence[] getItems() {
                return new String[]{"Input 1", "Input 2", "Input 3", "Input 4", "Input 5"};
            }

            @Override
            public void onSelected(int item) {
                int id;
                switch (item) {
                    default:
                    case 0:
                        id = R.raw.input1;
                        break;
                    case 1:
                        id = R.raw.input2;
                        break;
                    case 2:
                        id = R.raw.input3;
                        break;
                    case 3:
                        id = R.raw.input4;
                        break;
                    case 4:
                        id = R.raw.input5;
                        break;
                }

                if (create) {
                    tree = createTree(new JSONParser(getRawJSON(id)));
                } else {
                    addNodes(tree, new JSONParser(getRawJSON(id)));
                }
                showTreeStatus();
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create:
                DialogUtil.fileChooser(this, getFileChooserListener(true));
                break;
            case R.id.add:
                DialogUtil.fileChooser(this, getFileChooserListener(false));
                break;
            case R.id.remove:
                if (null == tree) {
                    Toast.makeText(this, "Create a tree first to continue",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                util = new RemoveDialogUtil(this, getColorListener(), getValueListener());
                util.show();
                break;
            case R.id.state:
                if (tree != null) {
                    final JSONArray state = getState(tree);
                    Log.d(TAG, "Current State : " + state.toString());
                    Toast.makeText(this, "Please check logs", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Create a tree first to continue",
                            Toast.LENGTH_SHORT).show();
                }
        }
    }
}
