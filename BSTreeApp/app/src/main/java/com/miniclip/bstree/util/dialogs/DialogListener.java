package com.miniclip.bstree.util.dialogs;

public interface DialogListener {
    CharSequence[] getItems();
    void onSelected(int item);
}
