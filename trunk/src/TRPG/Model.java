package TRPG;

import TRPG.ModelData;

import java.util.WeakHashMap;

public class Model{
	private static WeakHashMap<String,ModelData> models = new WeakHashMap<String,ModelData>();
	private ModelData data;
	private String name;
	public float xpos = 0f;
	public float ypos = 0f;
	public float zpos = 0f;
	public boolean applyScaling = false;
	public float xscale = 1f;
	public float yscale = 1f;
	public float zscale = 1f;

	public Model(String name){
		this.name = name;
		this.load();
	}
	public Model(String name, float xpos, float ypos, float zpos){
		this.name = name;
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
		this.load();
	}

	public void load(){
		// Check if model is already loaded
		this.data = (ModelData)Model.models.get(this.name);
		if(this.data != null){
			return;
		}
		this.data = new ModelData(this.name);
		Model.models.put(this.name,this.data);
		return;
	}

	public void render(){
		this.data.render(this);
	}

	public void render(float xpos, float ypos, float zpos){
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
		this.data.render(this);
	}
}