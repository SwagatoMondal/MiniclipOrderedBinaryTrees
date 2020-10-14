#include <jni.h>
#include <string>
#include <android/log.h>


jclass parser_cls;
jclass tree_cls;

jmethodID parser_length, parser_value, parser_color;
jmethodID tree_init, tree_get_left, tree_get_right, tree_set_left, tree_set_right, tree_value;

void init(JNIEnv* env) {
    parser_cls = env->FindClass("com/miniclip/bstree/util/JSONParser");
    tree_cls = env->FindClass("com/miniclip/bstree/entity/BSTree");

    parser_length = env->GetMethodID(parser_cls, "length", "()I");
    parser_value = env->GetMethodID(parser_cls, "getValue", "(I)I");
    parser_color = env->GetMethodID(parser_cls, "getColor", "(I)Ljava/lang/String;");

    tree_init = env->GetMethodID(tree_cls, "<init>", "(ILjava/lang/String;)V");
    tree_value = env->GetMethodID(tree_cls, "getValue", "()I");
    tree_get_left = env->GetMethodID(tree_cls, "getLeft", "()Lcom/miniclip/bstree/entity/BSTree;");
    tree_get_right = env->GetMethodID(tree_cls, "getRight", "()Lcom/miniclip/bstree/entity/BSTree;");
    tree_set_left = env->GetMethodID(tree_cls, "setLeft","(Lcom/miniclip/bstree/entity/BSTree;)V");
    tree_set_right = env->GetMethodID(tree_cls, "setRight","(Lcom/miniclip/bstree/entity/BSTree;)V");
}

jobject getNode(jobject node, int value, JNIEnv *env) {
    jobject left = env->CallObjectMethod(node, tree_get_left);
    jobject right = env->CallObjectMethod(node, tree_get_right);
    jint node_val = env->CallIntMethod(node, tree_value);

    // Return if already exists
    if (value == node_val) {
        return node;
    }

    if (value < node_val) {
        if (left == nullptr) {
            return node;
        } else {
            return getNode(left, value, env);
        }
    } else {
        if (right == nullptr) {
            return node;
        } else {
            return getNode(right, value, env);
        }
    }
}

void
createNodes(int length, int currentItem, JNIEnv *env, jobject parser, jobject root) {
    int value = env->CallIntMethod(parser, parser_value, currentItem);
    jstring color = (jstring) env->CallObjectMethod(parser, parser_color, currentItem);
    jobject node = env->NewObject(tree_cls, tree_init, value, color);

    jobject obj = getNode(root, value, env);
    jint obj_val = env->CallIntMethod(obj, tree_value);

    // Skip if already exists
    if (value != obj_val) {
        if (value <= obj_val) {
            // Add to left
            env->CallVoidMethod(obj, tree_set_left, node);
        } else {
            // Add to right
            env->CallVoidMethod(obj, tree_set_right, node);
        }
    }

    if (currentItem+1 < length) {
        createNodes(length, currentItem+1, env, parser, root);
    }
}

/**
 * Method to create a BSTree.
 * Input : A JSONParser object to read the values from JSONArray
 * Returns : A BSTree object representing the root of the tree
 */
extern "C" JNIEXPORT jobject JNICALL
Java_com_miniclip_bstree_MainActivity_createTree(
        JNIEnv* env,
        jobject /* this */,
        jobject parser) {
    __android_log_print(ANDROID_LOG_DEBUG, "Native-lib", "Create tree called");

    // Initializing
    init(env);

    int length = env->CallIntMethod(parser, parser_length);

    if (length == 0) {
        return nullptr;
    }

    // Create root node
    int root_value = env->CallIntMethod(parser, parser_value, 0);
    jstring root_color = (jstring) env->CallObjectMethod(parser, parser_color, 0);
    jobject root = env->NewObject(tree_cls, tree_init, root_value, root_color);

    if (length == 1) {
        return root;
    }

    createNodes(length, 1, env, parser, root);
    return root;
}

extern "C" JNIEXPORT void JNICALL
Java_com_miniclip_bstree_MainActivity_addNodes(
        JNIEnv* env,
        jobject /* this */,
        jobject tree,
        jobject parser) {
    __android_log_print(ANDROID_LOG_DEBUG, "Native-lib", "Add nodes called");
    // Initializing
    init(env);
    int length = env->CallIntMethod(parser, parser_length);
    if (length == 0) {
        return;
    }

    createNodes(length, 0, env, parser, tree);
}

jint minValue(jobject root, JNIEnv* env) {
    __android_log_print(ANDROID_LOG_ERROR, "Native-lib", "minValue start");
    int minVal = env->CallIntMethod(root, tree_value);
    __android_log_print(ANDROID_LOG_ERROR, "Native-lib", "minValue after start get val");
    jobject left = env->CallObjectMethod(root, tree_get_left);
    __android_log_print(ANDROID_LOG_ERROR, "Native-lib", "minValue before while");
    while (left != nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, "Native-lib", "minValue before get val");
        minVal = env->CallIntMethod(left, tree_value);
        __android_log_print(ANDROID_LOG_ERROR, "Native-lib", "minValue before get left");
        left = env->CallObjectMethod(left, tree_get_left);
    }
    __android_log_print(ANDROID_LOG_ERROR, "Native-lib", "minValue end while");

    return minVal;
}

jobject removeVal(jobject root, int value, JNIEnv* env) {
    if (nullptr == root) return root;

    int root_val = env->CallIntMethod(root, tree_value);
    jobject left = env->CallObjectMethod(root, tree_get_left);
    jobject right = env->CallObjectMethod(root, tree_get_right);
    if (value < root_val) {
        jobject node = removeVal(left, value, env);
        env->CallVoidMethod(root, tree_set_left, node);
    } else if (value > root_val) {
        jobject node = removeVal(right, value, env);
        env->CallVoidMethod(root, tree_set_right, node);
    } else {
        if (nullptr == left) {
            return right;
        } else if (nullptr == right) {
            return left;
        } else {
            jmethodID setValue = tree_value = env->GetMethodID(tree_cls, "setValue", "(I)V");
            jint minVal = minValue(right, env);
            env->CallVoidMethod(root, setValue, minVal);
            __android_log_print(ANDROID_LOG_ERROR, "Native-lib", "Remove value after set value");
            jobject node = removeVal(right, root_val, env);
            env->CallVoidMethod(root, tree_set_right, node);
        }
    }

    return root;
}

extern "C" JNIEXPORT void JNICALL
Java_com_miniclip_bstree_MainActivity_removeValue(
        JNIEnv* env,
        jobject /* this */,
        jobject tree,
        jint value) {
    __android_log_print(ANDROID_LOG_DEBUG, "Native-lib", "Remove value called");
    init(env);
    removeVal(tree, value, env);
}

extern "C" JNIEXPORT void JNICALL
Java_com_miniclip_bstree_MainActivity_removeColor(
        JNIEnv* env,
        jobject /* this */,
        jobject tree,
        jstring color) {
    init(env);
    __android_log_print(ANDROID_LOG_DEBUG, "Native-lib", "Remove color called");
}
