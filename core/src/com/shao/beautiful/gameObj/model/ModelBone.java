package com.shao.beautiful.gameObj.model;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.shao.beautiful.tools.TextureManager;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.Texture;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

public class ModelBone {
    public static final int CONVERSATION=16;
    private ModelInstance bone;
	private Matrix4 transform;
	private final Vector3 tmpG=new Vector3();
	
    private ModelBone(ModelBone another){
        this.bone=another.bone;
        
        transform=another.transform.cpy();
    }
    public ModelBone(ModelBuilder modelBuilder, float x, float y, float z,
                     float w, float h, float l,
                     Texture texture,int uv_x,int uv_y) {
        float x2=x/CONVERSATION,y2=y/CONVERSATION,z2=z/CONVERSATION,
        w2=w/CONVERSATION,h2=h/CONVERSATION,l2=l/CONVERSATION;
        
        float[][] p=new float[][]{
            new float[]{x2,y2,z2},
            new float[]{x2+w2,y2,z2},
            new float[]{x2,y2+h2,z2},
            new float[]{x2+w2,y2+h2,z2},
            new float[]{x2,y2,z2+l2},
            new float[]{x2+w2,y2,z2+l2},
            new float[]{x2,y2+h2,z2+l2},
            new float[]{x2+w2,y2+h2,z2+l2}
        };
        
        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.Tangent;
        
        modelBuilder.begin();
        
        //Back
        build(modelBuilder,getTextureReg(texture,uv_x+2*l+w,uv_y+l,w,h),p[1],p[0],p[2],p[3],0,0,-1);
        //Front
        build(modelBuilder,getTextureReg(texture,uv_x+l,uv_y+l,w,h),p[4],p[5],p[7],p[6],0,0,1);
        //Bottom
        build(modelBuilder,getTextureReg(texture,uv_x+l+w,uv_y,w,l),p[0],p[1],p[5],p[4],0,-1,0);
        //Top
        build(modelBuilder,getTextureReg(texture,uv_x+l,uv_y,w,l),p[6],p[7],p[3],p[2],0,1,0);
        //Left
        build(modelBuilder,getTextureReg(texture,uv_x,uv_y+l,l,h),p[0],p[4],p[6],p[2],-1,0,0);
        //Right
        build(modelBuilder,getTextureReg(texture,uv_x+l+w,uv_y+l,l,h),p[5],p[1],p[3],p[7],1,0,0);
        
        this.bone=new ModelInstance(modelBuilder.end());
        this.transform=bone.transform.cpy();
        //this.bone.transform.translate(x,y,z);
    }
    
    public void setPosition(float x, float y, float z) {
        transform.setTranslation(x,y,z);
    }
    public void rotate(Vector3 c,Vector3 d) {
    	
    	//transform.setTranslation(tmpV.set(c).scl(-1));
    	//System.out.println(transform.getTranslation(tmpV));
    	
    	transform.translate(tmpG.set(c).scl(1f/CONVERSATION));
    	Vector3 coordinateX=Vector3.X.cpy();
    	Vector3 coordinateY=Vector3.Y.cpy();
    	Vector3 coordinateZ=Vector3.Z.cpy();
		transform.rotate(coordinateX,d.x);
		coordinateY.rotate(coordinateX, d.x);
		coordinateZ.rotate(coordinateZ, d.x);
		transform.rotate(coordinateY,d.y);
		coordinateZ.rotate(coordinateY, d.y);
		transform.rotate(coordinateZ, d.z);
		
		transform.translate(tmpG.set(c).scl(-1f/CONVERSATION));
	}
    private Vector3 tmpV=new Vector3();
    public void addPosition(float x, float y, float z) {
        transform.setTranslation(transform.getTranslation(tmpV).add(x, y, z)); 
    }
    public void addPosition(Vector3 v) {
        transform.setTranslation(transform.getTranslation(tmpV).add(v)); 
    }
    public TextureRegion getTextureReg(Texture texture,float a,float b,float c,float d){
        return new TextureRegion(texture,(int)a,(int)b,(int)c,(int)d);
    }
   
    private void build(ModelBuilder modelBuilder, TextureRegion texture, float[] p1, float[] p2, float[] p3, float[] p4, float a, float b, float c) {
        int attr=VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        modelBuilder.part("box"+Math.random()*100, GL20.GL_TRIANGLES, attr, new Material(PBRTextureAttribute.createBaseColorTexture(texture)))
            .rect(p1[0], p1[1], p1[2],
                   p2[0], p2[1], p2[2],
                   p3[0], p3[1], p3[2],
                   p4[0], p4[1], p4[2],
                   a,b,c
                   );

    }
    public ModelBone copy() {
        return new ModelBone(this);
    }
    public ModelInstance get(){
    	bone.transform=transform;
        return bone;
    }
	public Vector3 getPosition() {
		return transform.getTranslation(new Vector3());
	}
    
}
