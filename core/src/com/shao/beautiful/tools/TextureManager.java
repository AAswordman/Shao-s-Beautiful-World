package com.shao.beautiful.tools;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class TextureManager {
    
    private static HashMap<String,Texture> texture=new HashMap<>();
    
    public static Texture get(FileHandle p1) {
        if (texture.containsKey(p1.path())) {
            return (Texture) texture.get((p1.path()));
        } else {
            Texture texx=new Texture(p1);
            texture.put(p1.path(), texx);
            
            
            return texx;
        }
	}
    
}
