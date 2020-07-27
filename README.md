# OpenGL Scene Graph Animated Lamp
This is a Java program involves using [OpenGL 3.x](https://www.opengl.org/) to render a scene, which contains an animated lamp. The scene graph was using in the modelling process, and the animation controls are implemented for the hierarchical model — the lamp.

The project is my previous coursework in the masters study period.

### **Demo**
Click the screenshot below to view the project demo. 

[![Demo](http://img.youtube.com/vi/9Z1MMNa8xEM/0.jpg)](http://www.youtube.com/watch?v=9Z1MMNa8xEM "AmiLamp Demo")

Note that the animation of lamp in the video demo is not smooth, and the lamp jumped wired sometimes. It was due to system recording and would not happen when you consume the project locally.

### **Scene and Scene Graph**
The scene includes:
- A plot of floor.
- A wall.
- A piece of an outside scene that could be seen through the window on the wall (the moving texture outside the window is fog).
- A lamp with a light bulb, which is positioned on the tabletop.
- A table located on the floor under the window on the wall.
- Three other objects (not including the lamp) on the table.
- Two global static lights.
- A virtual camera for viewing and controlling.

These objects are made of primitive objects only — basic triangles, cubes and spheres, and
they are modelled either by linear modelling method (modelling objects directly without
using scene graph) or hierarchical modelling method (scene graph), which are categorised as
the table below:

<table>
<tr>
    <td>Object</td>
    <td>Modelling Method</td>
</tr>
<tr>
    <td>Floor</td>
    <td rowspan="8">Linear Modelling Method (modelling objects
        directly without using scene graph)</td>
</tr>
<tr>
    <td>Outside Scene</td>
</tr>
<tr>
    <td>The Book on the Table</td>
</tr>
<tr>
    <td>The Globe on the Table</td>
</tr>
<tr>
    <td>The Mobile Phone on the Table</td>
</tr>
<tr>
    <td>Global Light 1</td>
</tr>
<tr>
    <td>Global Light 2</td>
</tr>
<tr>
    <td>Viewing Camera</td>
</tr>
<tr>
    <td>The Lamp on the Table</td>
    <td rowspan="4">Hierarchical Modelling Method (Scene Graph)</td>
</tr>
<tr>
    <td>The Light Bulb in the Lamp</td>
</tr>
<tr>
    <td>Table</td>
</tr>
<tr>
    <td>Wall</td>
</tr>
</table>

#### Scene Graph Example — Lamp
The angle-poise lamp is a hierarchical model, which is made up of the four basic parts:
- A base.
- A lower arm.
- An upper arm.
- A head which contains a protruding light bulb.

![Components](/pics/lamp-components.png)

Every part of lamp
connects at joints, so we can specify the model by giving all joint angles and positions.
The lower arm can rotate about the base, the upper arm can rotate about the lower arm, and the head can rotate about the upper arm. The light bulb shines in the same direction that the head is pointing in. Imagine the lamp is standing on a tabletop in a room looking out of a window. The lamp can also jump around the tabletop.

![Structure](/pics/lamp-structure.png)

In terms of code set up the scene graph, we are using Object-Oriented Design to construct scene graph nodes to represent, record and manipulate the hierarchical relationship in an 3D object in Java.

![Node Structure](/pics/node-structure.png)

The diagram below shows the class structure for an `SGNode` (Scene Graph Node). The link from an `SGNode` to itself is an indicator
that this is a recursive structure. In fact, an `SGNode` contains an `ArrayList` of child nodes, each of which is an `SGNode`.

Three other classes extend `SGNode`: a `ModelNode` contains a reference to a `Model` instance; a `NameNode` is used solely to make the
scene graph hierarchy clearer by allowing nodes that contain nothing but a `String` to represent their name; and a `TransformNode` is used to
represent a transformation (a `Mat4` instance) to be applied to its children in the scene graph. 

![SGNode Inheritance](/pics/scene-graph-node.jpg)

Comparing this structure to the scene graph of lamp, we
have a mixture of `NameNode`s (lamp base, lamp lower arm, lamp head joint, etc), `ModelNode`s (cube) and `TransformNode`s (lamp base rotate, Transform cube to make lower branch, lamp upper joint z rotate, etc). This is reflected in the
code in Program Listing below. `SGNode` contains a method called `addChild()` which is used to build the scene graph. I've used indentation in
the program code to make the hierarchy clearer, although some may argue that it makes the code a little more difficult to read. It's personal
choice.

```
0 Name: lamp root
1   Name: lamp transform
2       Name: lamp base scale
3           Name: lamp base
4               Name: lamp base 1
4               Name: lamp base rotate
5                   Name: lamp base 2
2       Name: lamp joint scale
3           Name: lamp lower joint
4               Name: lamp lower joint translate
5                   Name: lamp lower joint y rotate
6                       Name: lamp lower joint z rotate
7                           Name: lamp lower joint
7                           Name: lamp lower arm
8                               Name: lamp arm scale
9                                   Name: lamp lower arm translate
10                                      Name: lamp lower arm
8                               Name: lamp upper joint translate
9                                   Name: lamp upper joint y rotate
10                                      Name: lamp upper joint z rotate
11                                          Name: lamp upper joint
12                                              Name: lamp upper joint
12                                              Name: lamp upper arm
13                                                  Name: lamp upper arm translate
14                                                      Name: lamp upper arm
13                                                  Name: lamp head joint translate
14                                                      Name: lamp head joint y rotate
15                                                          Name: lamp head joint z rotate
16                                                              Name: lamp head joint
17                                                                  Name: lamp head joint self scale
18                                                                      Name: lamp head joint
17                                                                  Name: lamp head translate
18                                                                      Name: lamp head y rotate
19                                                                          Name: lamp head z rotate
20                                                                              Name: lamp head
21                                                                                  Name: lamp head self scale
22                                                                                      Name: lamp head
18                                                                      Name: lamp head transform
19                                                                          Name: lamp head back
20                                                                              Name: lamp head back
17                                                                  Name: lamp head ear left transform
18                                                                      Name: lamp head ear left
19                                                                          Name: lamp head ear left
17                                                                  Name: lamp head ear right transform
18                                                                      Name: lamp head ear right
19                                                                          Name: lamp head ear right
9                                   Name: lamp tail x rotate
10                                      Name: lamp tail y rotate
11                                          Name: lamp tail z rotate
12                                              Name: lamp tail
13                                                  Name: lamp tail transform
14                                                      Name: lamp tail
```

The visualised scene graph is shown below:

![Scene Graph](/pics/scene-graph-lamp.png)

### **Interaction**
- The lamp can change different poses plausibly by pressing the "Random Pose" button.
- Every time the lamp jumps, by pressing the "Jump" button, it will reset its pose at first, then jumping to a random place.
- The buttons, "Random Pose" and "Jump", will be disabled for about 2 seconds after the action of the lamp has completed.
- Please wait for the lamp to complete the jump (back in the table and restore the initial pose), then press the "Random Pose" or "Jump" button again, or the lamp will change the direction incorrectly in the next time.
- The lamp can avoid intersecting other three objects on the table when jumping.
- There are two objects on the table have a combination of specular and diffuse texture maps -- the lamp (the diffuse base and the specular arms) and the mobile phone (the diffuse frame and the specular screen).

### **Set Up Guide**
- The program displays at 1920\*1080 resolution in the video sample. I have changed the resolution to 1280\*720 to ensure the user can see the buttons below the window intuitively when the run the code in your laptop. If you still cannot see the control buttons, please maximise the window.
- It could take a while (roughly 10 seconds) for the window to display the scene since it needs to load textures.

### **Limitations**

### **License and Copyright**
The copyright of texture images states in the *.txt file in the textures folder.