package edu.servicemix.esb.services.transformer;

public class Transform {

    private String name = "Transform";

    public String doTransformation() {
        return "Transformer " + name + " is alive";
    }

    public void setName(String name) {
        this.name = name;
    }
}
