package com.miniclip.bstree.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashSet;

/**
 * Class to represent a node in the Binary Search Tree
 */
public class BSTree {
    private int value;
    private String color;
    private BSTree left, right;

    public BSTree(int value, String color) {
        this.value = value;
        this.color = color;
    }

    public void update(@NonNull BSTree tree) {
        this.color = tree.color;
        this.value = tree.value;
    }

    /**
     * Set the value of the current node
     * @param value New value
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Get the value of the current node
     * @return the value of the current node
     */
    public int getValue() {
        return value;
    }

    /**
     * Get the color of the current node
     * @return the color of the current node
     */
    public String getColor() {
        return color;
    }

    /**
     * Get the {@link BSTree} left node
     * @return Get the {@link BSTree} left node
     */
    public BSTree getLeft() {
        return left;
    }

    /**
     * Set the {@link BSTree} left node
     */
    public void setLeft(BSTree left) {
        this.left = left;
    }

    /**
     * Get the {@link BSTree} right node
     * @return Get the {@link BSTree} right node
     */
    public BSTree getRight() {
        return right;
    }

    /**
     * Set the {@link BSTree} right node
     */
    public void setRight(BSTree right) {
        this.right = right;
    }

    /**
     * Method to get a inorder representation of the tree as String
     * @param node Root node
     * @param builder Builder to create the string representation
     */
    public static void inorder(BSTree node, StringBuilder builder) {
        if (null == node) return;

        inorder(node.left, builder);
        builder.append(node.value).append(",");
        inorder(node.right, builder);
    }

    /**
     * Method to get the unique set of colors in a particular given tree
     * @param node Root node
     * @param colors {@link HashSet} to populate the values
     */
    public static void uniqueColors(@Nullable BSTree node, @NonNull HashSet<String> colors) {
        if (null == node) return;

        uniqueColors(node.left, colors);
        colors.add(node.color);
        uniqueColors(node.right, colors);
    }

    /**
     * Method to get the unique set of values in a particular given tree
     * @param node Root node
     * @param values {@link HashSet} to populate the values
     */
    public static void uniqueValues(@Nullable BSTree node, @NonNull HashSet<Integer> values) {
        if (null == node) return;

        uniqueValues(node.left, values);
        values.add(node.value);
        uniqueValues(node.right, values);
    }
}
