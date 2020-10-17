# Binary Search Tree App
This app consists of two components -
1. Native component
2. Java component

Rules :
The tree manipulation doesn't allow addition of duplication value nodes, but duplicate color nodes are allowed.

## Native component
The native component is responsible for doing the following operations -
1. Create Tree
2. Add to existing tree
3. Remove nodes from the given tree by color/value
4. Get current state of the tree

NOTE : All the operations assume that there's a JAVA class `BSTree` and a helper JAVA class `JSONParser`.

### Create Tree
The create API takes in a `JSONParser` object and returns back a `BSTree` object representing the root of the tree. The `JSONParser`
object contains a `JSONArray` representation of the nodes to be used to create the tree.

### Add to existing tree
The add API takes in a `JSONParser` object and a `BSTree` object representing the root of the tree. The `JSONParser`
object contains a `JSONArray` representation of the nodes to be used to add nodes to the existing tree.

### Remove nodes (value/color)
The remove API(s) takes in a `BSTree` object representing the root of the given tree and the
`value/color` to be removed from the tree. The API(s) then return the updated root of the tree.

### Current state
The state API takes in a `BSTree` object and returns a inorder traversal representation of the given tree in a `JSONArray` format.

## Java component
The Android app is responsible to render the tree which is completely manipulated by the Native component. The app
contains an activity named `MainActivity` and gives user the capability to perform all the following functions as mentioned in the Native component via buttons.
As soon as the app launches, the buttons will appear at the bottom of the screen and the user can perform the actions
by pressing the required button.

You can also view the rendered tree in 2 ways :
1. Inorder (Click on any node to see it and it's child nodes)
2. Tree View (Typical tree view)
This can be achieved by using the menu.

### Create and Add
The create and add options allows you to choose from 5 raw files already available in the app. Please choose a file
in order to complete the operation. Note the files are already pre-loaded with some values, please change or update
if required before deploying the app.

### Remove
The app will show the user a chooser to select the method (value/color) for removal of nodes. Once, the user
selects the **value/color** option, the app will show another pop-up with the unique set of possible values by which the
operation can be completed.

### Status
Once the user selects the **State** option, then the `JSONArray` representation of the tree is printed in the logs.