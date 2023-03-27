/*------------------------------------------------------------------------------
 * Copyright (C) 2014 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.jfx.layout;

import javafx.beans.binding.DoubleExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * This class provide an anchor mechanism similar to the one which is used in QML. However, contrary to
 * QML, it also manage several Shape classes. Shapes which are natively managed are:
 * <ul>
 * <li>{@link Region}</li>
 * <li>{@link javafx.scene.shape.Rectangle}</li>
 * <li>{@link javafx.scene.shape.Circle}</li>
 * <li>{@link javafx.scene.shape.Ellipse}</li>
 * <li>{@link javafx.scene.shape.Arc}</li>
 * <li>{@link javafx.scene.text.Text}</li>
 * </ul>
 *
 * @since 0.8
 */
public class NodeAnchor {
   private final Node node;
   private static final short TOP_INSIDE = 0;
   private static final short TOP_OUTSIDE = 1;
   private static final short BOTTOM = 2;
   private static final short RIGHT = 3;
   private static final short LEFT = 4;
   
   /**
    * The AnchorUtilities used internally to compute positions and sizes for Nodes.
    */
   protected AnchorUtilities utils = new DefaultAnchorUtilities();

   /**
    * Constructor. By default the {@link AnchorUtilities} will be a {@link DefaultAnchorUtilities}.
    *
    * @param node the Node which will be anchored
    */
   public NodeAnchor(Node node) {
      this.node = node;
   }

   /**
    * Constructor.
    *
    * @param node the Node which will be anchored
    * @param utils the class which will manage the position and size of Nodes
    */
   public NodeAnchor(Node node, AnchorUtilities utils) {
      this.node = node;
      this.utils = utils;
   }

   /**
    * Set the class which will manage the position and size of Nodes.
    *
    * @param utils the class which will manage the position and size of Nodes
    */
   public void setAnchorUtilities(AnchorUtilities utils) {
      this.utils = utils;
   }

   /**
    * Return the class which will manage the position and size of Nodes
    *
    * @return the class which will manage the position and size of Nodes
    */
   public AnchorUtilities getAnchorUtilities() {
      return utils;
   }

   /**
    * Set a Fill anchor. It is equivalent to call four times
    * {@link #anchor(Node, AnchorPosition, AnchorPosition)} with:
    * <ul>
    * <li>{@link AnchorPosition#LEFT} and {@link AnchorPosition#LEFT}</li>
    * <li>{@link AnchorPosition#RIGHT} and {@link AnchorPosition#RIGHT}</li>
    * <li>{@link AnchorPosition#TOP} and {@link AnchorPosition#TOP}</li>
    * <li>{@link AnchorPosition#BOTTOM} and {@link AnchorPosition#BOTTOM}</li>
    * </ul>
    *
    * @param nodeRef the reference Node
    */
   public void fill(Node nodeRef) {
      anchor(nodeRef, AnchorPosition.LEFT, AnchorPosition.LEFT);
      anchor(nodeRef, AnchorPosition.RIGHT, AnchorPosition.RIGHT);
      anchor(nodeRef, AnchorPosition.TOP, AnchorPosition.TOP);
      anchor(nodeRef, AnchorPosition.BOTTOM, AnchorPosition.BOTTOM);
   }

   /**
    * Set a Fill anchor relative to the Node parent. It is equivalent to call four times
    * {@link #anchor(Node, AnchorPosition, AnchorPosition)} with:
    * <ul>
    * <li>{@link AnchorPosition#LEFT} and {@link AnchorPosition#LEFT}</li>
    * <li>{@link AnchorPosition#RIGHT} and {@link AnchorPosition#RIGHT}</li>
    * <li>{@link AnchorPosition#TOP} and {@link AnchorPosition#TOP}</li>
    * <li>{@link AnchorPosition#BOTTOM} and {@link AnchorPosition#BOTTOM}</li>
    * </ul>
    */
   public void fillInParent() {
      Node parent = node.getParent();
      fill(parent);
   }

   /**
    * Set an anchor.
    *
    * @param nodeRef the anchor of the anchored Node
    * @param nodeAnchor the anchor of the Node
    * @param refAnchor the anchor of the reference Node
    */
   public void anchor(Node nodeRef, AnchorPosition nodeAnchor, AnchorPosition refAnchor) {
      if (node instanceof Region && nodeRef instanceof Region) {
         anchorRegion((Region) node, (Region) nodeRef, nodeAnchor, refAnchor, nodeRef == node.getParent());
      } else {
         anchorNode(node, nodeRef, nodeAnchor, refAnchor, nodeRef == node.getParent());
      }
   }

   /**
    * Set an anchor relative to the Node parent.
    *
    * @param nodeAnchor the anchor of the Node
    * @param refAnchor the anchor of the reference Node
    */
   public void anchorInParent(AnchorPosition nodeAnchor, AnchorPosition refAnchor) {
      Node parent = node.getParent();
      anchor(parent, nodeAnchor, refAnchor);
   }

   private void setHeightForHeightRef(final Node node, final Node nodeRef, final boolean isParent) {
      DoubleExpression property = utils.getHeightProperty(nodeRef);
      if (property != null) {
         property.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
               utils.setHeight(node, newValue.doubleValue());
            }
         });
      }
      property = utils.getHeightProperty(node);
      if (property != null) {
         property.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
               utils.setHeight(node, utils.getHeight(nodeRef));
            }
         });
      }
   }

   private void setWidthForWidthRef(final Node node, final Node nodeRef, final boolean isParent) {
      DoubleExpression property = utils.getWidthProperty(nodeRef);
      if (property != null) {
         property.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
               utils.setWidth(node, newValue.doubleValue());
            }
         });
      }
      property = utils.getWidthProperty(node);
      if (property != null) {
         property.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
               utils.setWidth(node, utils.getWidth(nodeRef));
            }
         });
      }
   }

   private void setLayoutXForWidthRef(final Node node, final Node nodeRef, final boolean isParent) {
      DoubleExpression property = utils.getWidthProperty(nodeRef);
      if (property != null) {
         property.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
               utils.setX(node, utils.getX(nodeRef) + newValue.doubleValue());
            }
         });
      }
   }

   private void setLayoutXForWidth(final Node node, final Node nodeRef, final short position, final boolean isParent) {
      DoubleExpression property = utils.getWidthProperty(node);
      if (property != null) {
         property.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
               if (!isParent) {
                  if (position == LEFT) {
                     utils.setX(node, utils.getX(nodeRef) - utils.getWidth(node));
                  } else {
                     utils.setX(node, utils.getX(nodeRef) + utils.getWidth(nodeRef) - newValue.doubleValue());
                  }
               } else {
                  if (position == LEFT) {
                     utils.setX(node, -utils.getWidth(node));
                  } else {
                     utils.setX(node, 0);
                  }
               }
            }
         });
      }
   }

   private void setLayoutYForHeightRef(final Node node, final Node nodeRef, final short position,
       final boolean isParent) {
      DoubleExpression property = utils.getHeightProperty(nodeRef);
      if (property != null) {
         property.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
               if (position == TOP_INSIDE) {
                  if (!isParent) {
                     utils.setY(node, utils.getY(nodeRef) - utils.getHeight(node));
                  } else {
                     utils.setY(node, -utils.getHeight(node));
                  }
               } else if (position == TOP_OUTSIDE) {
                  if (!isParent) {
                     utils.setY(node, utils.getY(nodeRef) + newValue.doubleValue() - utils.getHeight(node));
                  } else {
                     utils.setY(node, -utils.getHeight(node));
                  }
               }
            }
         });
      }
   }

   private void setLayoutYForHeight(final Node node, final Node nodeRef, final short position,
       final boolean isParent) {
      DoubleExpression property = utils.getHeightProperty(node);
      if (property != null) {
         property.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
               if (position == BOTTOM) {
                  if (!isParent) {
                     utils.setY(node, utils.getY(nodeRef) + utils.getHeight(nodeRef) - newValue.doubleValue());
                  } else {
                     utils.setY(node, 0);
                  }
               } else if (position == TOP_INSIDE) {
                  if (!isParent) {
                     utils.setY(node, utils.getY(nodeRef));
                  } else {
                     utils.setY(node, -utils.getHeight(node));
                  }
               } else if (position == TOP_OUTSIDE) {
                  if (!isParent) {
                     utils.setY(node, utils.getY(nodeRef) - newValue.doubleValue());
                  } else {
                     utils.setY(node, -utils.getHeight(node));
                  }
               }
            }
         });
      }
   }

   private void anchorNode(final Node node, final Node nodeRef, AnchorPosition nodeAnchor, AnchorPosition refAnchor,
       final boolean isParent) {
      if (nodeAnchor == AnchorPosition.LEFT) {
         if (refAnchor == AnchorPosition.LEFT) {
            utils.getXProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setX(node, newValue.doubleValue());
                  }
               }
            });
            utils.getYProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setY(node, newValue.doubleValue());
                  }
               }
            });
            utils.getXProperty(node).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setX(node, utils.getX(nodeRef));
                  }
               }
            });
            utils.getYProperty(node).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  utils.setY(node, utils.getY(nodeRef));
               }
            });
            setHeightForHeightRef(node, nodeRef, isParent);
         } else if (refAnchor == AnchorPosition.RIGHT) {
            utils.getXProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setX(node, newValue.doubleValue());
                  } else {
                     utils.setX(node, utils.getWidth(nodeRef));
                  }
               }
            });
            utils.getYProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setY(node, newValue.doubleValue());
                  } else {
                     utils.setY(node, 0);
                  }
               }
            });
            utils.getHeightProperty(node).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setY(node, utils.getY(nodeRef));
                  } else {
                     utils.setY(node, 0);
                  }
               }
            });

            utils.getYProperty(node).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setY(node, utils.getY(nodeRef));
                  } else {
                     utils.setY(node, 0);
                  }
               }
            });
            utils.getXProperty(node).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setX(node, utils.getX(nodeRef) + utils.getWidth(nodeRef));
                  } else {
                     utils.setX(node, utils.getWidth(nodeRef));
                  }
               }
            });
            setLayoutXForWidthRef(node, nodeRef, isParent);
            setHeightForHeightRef(node, nodeRef, isParent);
         }
      } else if (nodeAnchor == AnchorPosition.RIGHT) {
         if (refAnchor == AnchorPosition.LEFT) {
            utils.getXProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setX(node, newValue.doubleValue() - utils.getWidth(node));
                  } else {
                     utils.setX(node, -utils.getWidth(node));
                  }
               }
            });
            utils.getYProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setY(node, newValue.doubleValue());
                  } else {
                     utils.setY(node, 0);
                  }
               }
            });
            utils.getYProperty(node).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setY(node, utils.getY(nodeRef));
                  } else {
                     utils.setY(node, 0);
                  }
               }
            });
            setLayoutXForWidth(node, nodeRef, LEFT, isParent);
            setHeightForHeightRef(node, nodeRef, isParent);
         } else if (refAnchor == AnchorPosition.RIGHT) {
            utils.getXProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setX(node, newValue.doubleValue() + utils.getWidth(nodeRef) - utils.getWidth(node));
                  } else {
                     utils.setX(node, -utils.getWidth(nodeRef));
                  }
               }
            });
            utils.getYProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setY(node, newValue.doubleValue());
                     utils.setWidth(node, newValue.doubleValue() + utils.getX(node) - utils.getX(nodeRef));
                  } else {
                     utils.setX(node, -utils.getWidth(nodeRef));
                  }
               }
            });
            utils.getYProperty(node).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setY(node, utils.getY(nodeRef));
                  } else {
                     utils.setY(node, 0);
                  }
               }
            });
            setLayoutXForWidth(node, nodeRef, RIGHT, isParent);
            setHeightForHeightRef(node, nodeRef, isParent);
         }
      } else if (nodeAnchor == AnchorPosition.TOP) {
         if (refAnchor == AnchorPosition.BOTTOM) {
            utils.getYProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setY(node, newValue.doubleValue());
                  } else {
                     utils.setY(node, utils.getHeight(nodeRef));
                  }
               }
            });
            utils.getXProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setX(node, newValue.doubleValue());
                  } else {
                     utils.setX(node, 0);
                  }
               }
            });
            utils.getXProperty(node).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setX(node, utils.getX(nodeRef));
                  } else {
                     utils.setX(node, 0);
                  }
               }
            });
            utils.getYProperty(node).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setY(node, utils.getY(nodeRef) + utils.getHeight(nodeRef));
                  } else {
                     utils.setY(node, utils.getHeight(nodeRef));
                  }
               }
            });
            setLayoutYForHeightRef(node, nodeRef, TOP_OUTSIDE, isParent);
            setWidthForWidthRef(node, nodeRef, isParent);
         } else if (refAnchor == AnchorPosition.TOP) {
            utils.getYProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  utils.setY(node, newValue.doubleValue());
               }
            });
            utils.getXProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  utils.setX(node, newValue.doubleValue());
               }
            });
            utils.getXProperty(node).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  utils.setX(node, utils.getX(nodeRef));
               }
            });
            setLayoutYForHeight(node, nodeRef, TOP_INSIDE, isParent);
            setLayoutYForHeightRef(node, nodeRef, TOP_OUTSIDE, isParent);
            setWidthForWidthRef(node, nodeRef, isParent);
         }
      } else if (nodeAnchor == AnchorPosition.BOTTOM) {
         if (refAnchor == AnchorPosition.BOTTOM) {
            utils.getYProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  utils.setY(node, newValue.doubleValue() + utils.getHeight(node));
               }
            });
            utils.getXProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  utils.setX(node, newValue.doubleValue());
               }
            });
            utils.getXProperty(node).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  utils.setX(node, utils.getX(nodeRef));
               }
            });
            setLayoutYForHeight(node, nodeRef, BOTTOM, isParent);
            setLayoutYForHeightRef(node, nodeRef, TOP_OUTSIDE, isParent);
            setWidthForWidthRef(node, nodeRef, isParent);
         } else if (refAnchor == AnchorPosition.TOP) {
            utils.getYProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setY(node, newValue.doubleValue() - utils.getHeight(node));
                  } else {
                     utils.setY(node, -utils.getHeight(node));
                  }
               }
            });
            utils.getXProperty(nodeRef).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setX(node, newValue.doubleValue());
                  } else {
                     utils.setX(node, 0);
                  }
               }
            });
            utils.getXProperty(node).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  if (!isParent) {
                     utils.setX(node, utils.getX(nodeRef));
                  } else {
                     utils.setX(node, 0);
                  }
               }
            });
            utils.getWidthProperty(node).addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  utils.setWidth(node, utils.getWidth(nodeRef));
               }
            });
            setLayoutYForHeight(node, nodeRef, TOP_OUTSIDE, isParent);
            setLayoutYForHeightRef(node, nodeRef, TOP_INSIDE, isParent);
            setWidthForWidthRef(node, nodeRef, isParent);
         }
      }
   }

   private void anchorRegion(final Region region, final Region nodeRef, AnchorPosition nodeAnchor, AnchorPosition refAnchor,
       final boolean isParent) {
      if (nodeAnchor == AnchorPosition.LEFT) {
         if (refAnchor == AnchorPosition.LEFT) {
            nodeRef.layoutXProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutX(newValue.doubleValue());
               }
            });
            nodeRef.layoutYProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutY(newValue.doubleValue());
               }
            });
            nodeRef.heightProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setPrefHeight(newValue.doubleValue());
                  region.setMaxHeight(newValue.doubleValue());
                  region.setMinHeight(newValue.doubleValue());
               }
            });
         } else if (refAnchor == AnchorPosition.RIGHT) {
            nodeRef.layoutXProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutX(newValue.doubleValue() + nodeRef.getWidth());
               }
            });
            nodeRef.layoutYProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutY(newValue.doubleValue());
               }
            });
            nodeRef.widthProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutX(nodeRef.layoutXProperty().doubleValue() + newValue.doubleValue());
               }
            });
            nodeRef.heightProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setPrefHeight(newValue.doubleValue());
                  region.setMaxHeight(newValue.doubleValue());
                  region.setMinHeight(newValue.doubleValue());
               }
            });
         }
      } else if (nodeAnchor == AnchorPosition.RIGHT) {
         if (refAnchor == AnchorPosition.LEFT) {
            nodeRef.layoutXProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutX(newValue.doubleValue() - region.getWidth());
               }
            });
            nodeRef.layoutYProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutY(newValue.doubleValue());
               }
            });
            region.widthProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutX(nodeRef.getLayoutX() - region.getWidth());
               }
            });
            nodeRef.heightProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setPrefHeight(newValue.doubleValue());
                  region.setMaxHeight(newValue.doubleValue());
                  region.setMinHeight(newValue.doubleValue());
               }
            });
         } else if (refAnchor == AnchorPosition.RIGHT) {
            nodeRef.layoutXProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutX(newValue.doubleValue() + nodeRef.getWidth() - region.getWidth());
               }
            });
            nodeRef.layoutYProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutY(newValue.doubleValue());
                  double width = newValue.doubleValue() + region.getLayoutX() - nodeRef.getLayoutX();
                  region.setPrefWidth(width);
                  region.setMaxWidth(width);
                  region.setMinWidth(width);
               }
            });
            region.widthProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutX(nodeRef.getLayoutX() + nodeRef.getWidth() - newValue.doubleValue());
               }
            });
            nodeRef.heightProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setPrefHeight(newValue.doubleValue());
                  region.setMaxHeight(newValue.doubleValue());
                  region.setMinHeight(newValue.doubleValue());
               }
            });
         }
      } else if (nodeAnchor == AnchorPosition.TOP) {
         if (refAnchor == AnchorPosition.BOTTOM) {
            nodeRef.layoutXProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutX(newValue.doubleValue());
               }
            });
            nodeRef.layoutYProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutY(newValue.doubleValue() + nodeRef.getLayoutY());
               }
            });
            nodeRef.heightProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutY(nodeRef.layoutYProperty().doubleValue() + newValue.doubleValue());
               }
            });
            nodeRef.widthProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setPrefWidth(newValue.doubleValue());
                  region.setMaxWidth(newValue.doubleValue());
                  region.setMinWidth(newValue.doubleValue());
               }
            });
         } else if (refAnchor == AnchorPosition.TOP) {
            nodeRef.layoutYProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutY(newValue.doubleValue());
               }
            });
            nodeRef.layoutXProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutX(newValue.doubleValue());
               }
            });
            nodeRef.widthProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setPrefWidth(newValue.doubleValue());
                  region.setMaxWidth(newValue.doubleValue());
                  region.setMinWidth(newValue.doubleValue());
               }
            });
         }
      } else if (nodeAnchor == AnchorPosition.BOTTOM) {
         if (refAnchor == AnchorPosition.BOTTOM) {
            nodeRef.layoutXProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutX(newValue.doubleValue());
               }
            });
            nodeRef.layoutYProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutY(newValue.doubleValue() + nodeRef.getPrefHeight() - region.getPrefHeight());
               }
            });
            region.heightProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutY(nodeRef.getLayoutY() + nodeRef.getPrefHeight() - region.getPrefHeight());
               }
            });
            nodeRef.widthProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setPrefWidth(newValue.doubleValue());
                  region.setMaxWidth(newValue.doubleValue());
                  region.setMinWidth(newValue.doubleValue());
               }
            });
         } else if (refAnchor == AnchorPosition.TOP) {
            nodeRef.layoutYProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutY(newValue.doubleValue() - region.getPrefHeight());
               }
            });
            nodeRef.layoutXProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutX(newValue.doubleValue());
               }
            });
            region.heightProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setLayoutY(nodeRef.getLayoutY() - newValue.doubleValue());
               }
            });
            nodeRef.widthProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                  region.setPrefWidth(newValue.doubleValue());
                  region.setMaxWidth(newValue.doubleValue());
                  region.setMinWidth(newValue.doubleValue());
               }
            });
         }
      }
   }

}
