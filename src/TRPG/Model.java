package TRPG;

import TRPG.ModelData;

import java.util.WeakHashMap;

public class Model {
	private static WeakHashMap<String, ModelData> models = new WeakHashMap<String, ModelData>();
	private ModelData modelData;
	private String name;
	public float xpos = 0f;
	public float ypos = 0f;
	public float zpos = 0f;
	public boolean applyScaling = false;
	public float xscale = 1f;
	public float yscale = 1f;
	public float zscale = 1f;

	private static ModelData loadModelData(String name) {
		// Check if model is already loaded
		ModelData modelData = Model.models.get(name);
		if (modelData == null) {
			modelData = new ModelData(name);
			Model.models.put(name, modelData);
		}
		return modelData;
	}

	public Model(String name) {
		this.modelData = loadModelData(name);
		this.name = name;
	}

	public Model position(xpos, ypos, zpos) {
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
		return this;
	}

	public Model scale(float scale) {
		return this.scale(scale, scale, scale);
	}

	public Model scale(float xscale, float yscale, float zscale) {
		this.xscale = xscale;
		this.yscale = yscale;
		this.zscale = zscale;
		this.applyScaling = (xscale != 1f || yscale != 1f || zscale != 1f);
		return this;
	}

	public Model render() {
		this.modelData.render(this);
		return this;
	}
}
