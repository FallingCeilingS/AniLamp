# OpenGL Scene Graph Animated Lamp
This is a Java program involves using [OpenGL 3.x](https://www.opengl.org/) to render a scene, which contains an animated lamp. The scene graph was using in the modelling process, and the animation controls are implemented for the hierarchical model — the lamp.

The project is my previous coursework in the masters study period.

### **Demo**
Click the screenshot below to view the project demo. 

[![Demo](http://img.youtube.com/vi/9Z1MMNa8xEM/0.jpg)](http://www.youtube.com/watch?v=9Z1MMNa8xEM "AmiLamp")

Note that The animation of lamp in the video demo is not smooth, and the lamp jumped wired sometimes. It was due to system recording and would not happen when you consume the project locally.

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

#### Lamp
The angle-poise lamp is a hierarchical model, which is made up of the four basic parts:
- A base.
- A lower arm.
- An upper arm.
- A head which contains a protruding light bulb.

The lower arm can rotate about the base, the upper arm can rotate about the lower arm, and the head can rotate about the upper arm. The light bulb shines in the same direction that the head is pointing in. Imagine the lamp is standing on a tabletop in a room looking out of a window. The lamp can also jump around the tabletop.

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