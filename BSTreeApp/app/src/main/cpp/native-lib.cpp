#include <jni.h>
#include <string>
#include <android/log.h>


jclass tree_cls;

jmethodID parser_length, parser_value, parser_color;
jmethodID tree_init, tree_get_left, tree_get_right, tree_set_left, tree_set_right, tree_value,
    tree_update, tree_color;

void init(JNIEnv* env) {
    jclass parser_cls = env->FindClass("com/miniclip/bstree/util/JSONParser");
    tree_cls = env->FindClass("com/miniclip/bstree/entity/BSTree");

    parser_length = env->GetMethodID(parser_cls, "length", "()I");
    parser_value = env->GetMethodID(parser_cls, "getValue", "(I)I");
    parser_color = env->GetMethodID(parser_cls, "getColor", "(I)Ljava/lang/String;");

    tree_init = env->GetMethodID(tree_cls, "<init>", "(ILjava/lang/String;)V");
    tree_value = env->GetMethodID(tree_cls, "getValue", "()I");
    tree_color = env->GetMethodID(tree_cls, "getColor", "()Ljava/lang/String;");
    tree_update = env->GetMethodID(tree_cls, "update", "(Lcom/miniclip/bstree/entity/BSTree;)V");
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

/**
 * Method to add nodes to a BSTree.
 * Input : A JSONParser object to read the values from JSONArray, Root of the already existing tree
 * Returns : A BSTree object representing the root of the tree
 */
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

/**
 * Method to get the inorder successor of the given node. The method also updates the tree assuming
 * that this node will be replaced.
 * @param root Node whose inorder successor needs to be found out
 * @param env JNI environment
 * @return the inorder successor
 */
jobject minValue(jobject root, JNIEnv* env) {
    jobject node = root;
    jobject left = env->CallObjectMethod(root, tree_get_left);
    while (left != nullptr) {
        node = left;
        left = env->CallObjectMethod(left, tree_get_left);
    }

    return node;
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
            jobject minVal = minValue(right, env);
            env->CallVoidMethod(root, tree_update, minVal);
            int inorder_key = env->CallIntMethod(minVal, tree_value);
            jobject node = removeVal(right, inorder_key, env);
            env->CallVoidMethod(root, tree_set_right, node);
        }
    }

    return root;
}

bool compareString(jstring str1, jstring str2, JNIEnv* env) {
    jclass cls = (env)->GetObjectClass(str1);
    jmethodID mID = (env)->GetMethodID(cls, "equals", "(Ljava/lang/Object;)Z");
    return (env)->CallBooleanMethod(str1, mID, str2);
}

jobject removeColor(jobject root, jstring color, JNIEnv* env) {
    if (nullptr == root) return root;

    __android_log_print(ANDROID_LOG_ERROR, "Native-lib", "Remove color start");
    jstring root_color = (jstring) env->CallObjectMethod(root, tree_color);
    jobject left = env->CallObjectMethod(root, tree_get_left);
    jobject right = env->CallObjectMethod(root, tree_get_right);

    removeColor(left, color, env);
    removeColor(right, color, env);

    // Color match found
    __android_log_print(ANDROID_LOG_ERROR, "Native-lib", "Remove color before color match");
    if (compareString(color, root_color, env)) {
        __android_log_print(ANDROID_LOG_ERROR, "Native-lib", "Remove color inside color match");
        if (nullptr == left) {
            return right;
        } else if (nullptr == right) {
            return left;
        } else {
            __android_log_print(ANDROID_LOG_ERROR, "Native-lib", "Remove color inside color match else");
            jobject minVal = minValue(right, env);
            env->CallVoidMethod(root, tree_update, minVal);
            jstring inorder_color = (jstring) env->CallObjectMethod(minVal, tree_color);
            jobject node = removeColor(right, inorder_color, env);
            env->CallVoidMethod(root, tree_set_right, node);
        }
    }
    __android_log_print(ANDROID_LOG_ERROR, "Native-lib", "Remove color after color match");
    return root;
}

/**
 * Method to remove the given value from the tree
 * Input : Root of the tree, value to be removed
 * Output : Updated root
 */
extern "C" JNIEXPORT jobject JNICALL
Java_com_miniclip_bstree_MainActivity_removeValue(
        JNIEnv* env,
        jobject /* this */,
        jobject tree,
        jint value) {
    __android_log_print(ANDROID_LOG_DEBUG, "Native-lib", "Remove value called");
    init(env);
    return removeVal(tree, value, env);
}

/**
 * Method to remove the given color from the tree
 * Input : Root of the tree, color to be removed
 * Output : Updated root
 */
extern "C" JNIEXPORT jobject JNICALL
Java_com_miniclip_bstree_MainActivity_removeColor(
        JNIEnv* env,
        jobject /* this */,
        jobject tree,
        jstring color) {
    init(env);
    __android_log_print(ANDROID_LOG_DEBUG, "Native-lib", "Remove color called");
    return removeColor(tree, color, env);
}

void statePopulation(JNIEnv* env, jobject root, jobject state, jmethodID put) {
    if (nullptr == root) return;

    int root_val = env->CallIntMethod(root, tree_value);
    jstring root_color = (jstring) env->CallObjectMethod(root, tree_color);
    jobject left = env->CallObjectMethod(root, tree_get_left);
    jobject right = env->CallObjectMethod(root, tree_get_right);

    statePopulation(env, left, state, put);

    jclass json_cls = env->FindClass("org/json/JSONObject");
    jmethodID init = env->GetMethodID(json_cls, "<init>", "()V");
    jmethodID put_int_id = env->GetMethodID(json_cls, "put",
            "(Ljava/lang/String;I)Lorg/json/JSONObject;");
    jmethodID put_str_id = env->GetMethodID(json_cls, "put",
            "(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;");
    jobject node = env->NewObject(json_cls, init);
    env->CallObjectMethod(node, put_int_id, env->NewStringUTF("value"), root_val);
    env->CallObjectMethod(node, put_str_id, env->NewStringUTF("color"), root_color);
    env->CallObjectMethod(state, put, node);

    statePopulation(env, right, state, put);
}

/**
 * Method to get the state of the tree as a JSONArray
 * Input : Root the tree
 * Output : JSONArray representation of the inorder traversal of the tree
 */
extern "C" JNIEXPORT jobject JNICALL
Java_com_miniclip_bstree_MainActivity_getState(
        JNIEnv* env,
        jobject /* this */,
        jobject tree) {
    init(env);
    __android_log_print(ANDROID_LOG_DEBUG, "Native-lib", "Get state called");

    jclass json_cls = env->FindClass("org/json/JSONArray");
    jmethodID init = env->GetMethodID(json_cls, "<init>", "()V");
    jmethodID put = env->GetMethodID(json_cls, "put", "(Ljava/lang/Object;)Lorg/json/JSONArray;");
    jobject state = env->NewObject(json_cls, init);

    statePopulation(env, tree, state, put);

    return state;
}
