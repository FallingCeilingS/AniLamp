import com.jogamp.opengl.*;

public class ModelNode extends SGNode {

    protected Model model;

    public ModelNode(String name, Model m) {
        super(name);
        model = m;
    }

    public void draw(GL3 gl3) {
        model.render(gl3, worldTransform);
        for (int i = 0; i < children.size(); i++) {
            children.get(i).draw(gl3);
        }
    }

}