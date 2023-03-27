/*------------------------------------------------------------------------------
 * Copyright (C) 2014 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.jfx.layout;

import javafx.beans.binding.DoubleExpression;
import javafx.scene.Node;

/**
 * This interface compute positions and sizes for Nodes.
 *
 * @since 0.8
 */
public interface AnchorUtilities {
   /** 
    * Return the Y position of a Node.
    * 
    * @param node the Node
    * @return the Y position of the Node
    */
   public double getY(Node node);

   /** 
    * Return the X position of a Node.
    * 
    * @param node the Node
    * @return the X position of the Node
    */   
   public double getX(Node node);

   /**
    * Set the X position of a Node.
    * 
    * @param node the Node
    * @param value the X position
    */         
   public void setX(Node node, double value);

   /**
    * Set the Y position of a Node.
    * 
    * @param node the Node
    * @param value the Y position
    */      
   public void setY(Node node, double value);

   /**
    * Set the height of a Node.
    * 
    * @param node the Node
    * @param value the height
    */   
   public void setHeight(Node node, double value);

   /** 
    * Return the height of a Node.
    * 
    * @param node the Node
    * @return the height of the Node
    */   
   public double getHeight(Node node);

   /**
    * Set the width of a Node.
    * 
    * @param node the Node
    * @param value the width
    */
   public void setWidth(Node node, double value);

   /** 
    * Return the width of a Node.
    * 
    * @param node the Node
    * @return the width of the Node
    */      
   public double getWidth(Node node);

   /** 
    * Return the X property of a Node.
    * 
    * @param node the Node
    * @return the X property of the Node
    */              
   public DoubleExpression getXProperty(Node node);

   /** 
    * Return the Y property of a Node.
    * 
    * @param node the Node
    * @return the Y property of the Node
    */           
   public DoubleExpression getYProperty(Node node);

   /** 
    * Return the height property of a Node.
    * 
    * @param node the Node
    * @return the height property of the Node
    */     
   public DoubleExpression getHeightProperty(Node node);

   /** 
    * Return the width property of a Node.
    * 
    * @param node the Node
    * @return the width property of the Node
    */        
   public DoubleExpression getWidthProperty(Node node);
}
