/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.html.parser;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.vietspider.html.HTMLNode;
import org.vietspider.html.MoveType;
import org.vietspider.html.Name;
import org.vietspider.html.NodeConfig;
import org.vietspider.html.NodeConfigs;
import org.vietspider.html.Tag;
import org.vietspider.html.Group.Block;
import org.vietspider.html.Group.Default;
import org.vietspider.html.Group.Flow;
import org.vietspider.html.Group.Fontstyle;
import org.vietspider.html.Group.Formctrl;
import org.vietspider.html.Group.HeadContent;
import org.vietspider.html.Group.HeadMisc;
import org.vietspider.html.Group.Heading;
import org.vietspider.html.Group.HtmlContent;
import org.vietspider.html.Group.List;
import org.vietspider.html.Group.Phrase;
import org.vietspider.html.Group.Preformatted;
import org.vietspider.html.Group.Special;
import org.vietspider.html.Group.Table;
import org.vietspider.token.TypeToken;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 3, 2006
 */

@NodeConfigs({  
  @NodeConfig (name = Name.A, type = Special.class),
  @NodeConfig (name = Name.ABBR, type = Phrase.class),
  @NodeConfig (name = Name.ACRONYM, type = Phrase.class),
  @NodeConfig (name = Name.ADDRESS, type = Block.class),
  @NodeConfig (name = Name.APPLET, type = Special.class),
  @NodeConfig (name = Name.AREA, end = Tag.FORBIDDEN , type = Default.class),

  @NodeConfig (name = Name.B, type = Fontstyle.class),
  @NodeConfig (name = Name.BASE, end  = Tag.FORBIDDEN, parent = {Name.HEAD}, type = HeadContent.class),
  @NodeConfig (name = Name.BASEFONT, end  = Tag.FORBIDDEN, type = Special.class),
  @NodeConfig (name = Name.BDO, type = Special.class),
  @NodeConfig (name = Name.BIG, type = Fontstyle.class),
  @NodeConfig (name = Name.BLOCKQUOTE, type = Block.class),
  @NodeConfig (name = Name.BODY, 
      start = Tag.OPTIONAL, end = Tag.OPTIONAL,  parent = {Name.HTML},
      type = HtmlContent.class, only = true
  ),
  @NodeConfig (name = Name.HEADER, 
  start = Tag.OPTIONAL, end = Tag.OPTIONAL,  parent = {Name.HTML},
  type = HtmlContent.class, only = true
  ),
  @NodeConfig (name = Name.ARTICLE, 
  start = Tag.OPTIONAL, end = Tag.OPTIONAL,  parent = {Name.HTML},
  type = HtmlContent.class, only = true
  ),
  @NodeConfig (name = Name.USE, end = Tag.FORBIDDEN ,type = Special.class),
  
  @NodeConfig (name = Name.BR, end  = Tag.FORBIDDEN, type = Special.class),
  @NodeConfig (name = Name.BUTTON, type = Formctrl.class),

  @NodeConfig (name = Name.CAPTION, parent = {Name.TABLE}, type = Table.class),
  @NodeConfig (name = Name.CENTER, type = Block.class),
  @NodeConfig (name = Name.CITE, type = Phrase.class),
  @NodeConfig (name = Name.CODE, type = Phrase.class),
  @NodeConfig (name = Name.COL, end  = Tag.FORBIDDEN, parent = {Name.COLGROUP}, type = Default.class),
  @NodeConfig (name = Name.COLGROUP, 
      end = Tag.OPTIONAL, parent = {Name.TABLE}, children = {Name.COL}, type = Default.class
  ),

  @NodeConfig (name = Name.DD, 
      end = Tag.OPTIONAL, parent = {Name.DL}, type = Default.class, end_names = {Name.DT, Name.DD}
  ),
  @NodeConfig (name = Name.DEL, type = Default.class),
  @NodeConfig (name = Name.DFN, type = Phrase.class),
  @NodeConfig (name = Name.DIR, type = List.class),
  @NodeConfig (name = Name.DIV, type = Block.class),
  
  
  
  @NodeConfig (name = Name.SVG, type = Special.class),
  @NodeConfig (name = Name.STOP, type = Special.class),
  // </linearGradient/>
  @NodeConfig (name = Name.LINEARGRADIENT, type = Special.class),
  
  @NodeConfig (name = Name.G, type = Special.class),
  
  @NodeConfig (name = Name.TIME, type = Special.class),
  
  @NodeConfig (name = Name.PATH, type = Special.class),
  
  
  
  @NodeConfig (name = Name.DL, type = Block.class),
  @NodeConfig (name = Name.DT, 
      end = Tag.OPTIONAL, parent = {Name.DL}, type = Default.class, end_names = {Name.DT}
  ),

  @NodeConfig (name = Name.EM, type = Phrase.class),
  @NodeConfig (name = Name.EMBED,
      end  = Tag.FORBIDDEN, parent = {Name.OBJECT}, type = Default.class
  ),

  @NodeConfig (name = Name.FIELDSET, type = Block.class),
  @NodeConfig (name = Name.FONT, type = Special.class),
  @NodeConfig (name = Name.FORM, type = Block.class), //hidden = true, 
  @NodeConfig (name = Name.FRAME, 
		  end = Tag.OPTIONAL,
       parent = {Name.FRAMESET}, type = Default.class // end  = Tag.FORBIDDEN,
  ),
  @NodeConfig (name = Name.FRAMESET, children = {Name.FRAMESET, Name.FRAME, Name.NOFRAMES}, 
	  type = Default.class
  ),

  @NodeConfig (name = Name.H1, 
      end = Tag.OPTIONAL, type = Heading.class, end_types = {Heading.class}
  ),
  @NodeConfig (name = Name.H2, 
      end = Tag.OPTIONAL, type = Heading.class, end_types = {Heading.class}
  ),
  @NodeConfig (name = Name.H3,
      end = Tag.OPTIONAL, type = Heading.class, end_types = {Heading.class}
  ),
  @NodeConfig (name = Name.H4, 
      end = Tag.OPTIONAL, type = Heading.class, end_types = {Heading.class}
  ),
  @NodeConfig (name = Name.H5, 
      end = Tag.OPTIONAL, type = Heading.class, end_types = {Heading.class}
  ),
  @NodeConfig (name = Name.H6, 
      end = Tag.OPTIONAL, type = Heading.class, end_types = {Heading.class}
  ),
  @NodeConfig (name = Name.HEAD, 
      start = Tag.OPTIONAL, end = Tag.OPTIONAL, parent = {Name.HTML},
      children = {Name.TITLE, Name.META, Name.COMMENT, Name.LINK, Name.SCRIPT, Name.NOSCRIPT, Name.STYLE, Name.BASE},
      type = HtmlContent.class, only = true
  ),
  @NodeConfig (name = Name.HR, end  = Tag.FORBIDDEN, type = Block.class),
  @NodeConfig (name = Name.HTML, 
      start = Tag.OPTIONAL, end = Tag.OPTIONAL,  children = {Name.HEAD, Name.FRAMESET, Name.BODY },
      type = Default.class, only = true
  ),
  @NodeConfig (name = Name.I, type = Fontstyle.class),
  @NodeConfig (name = Name.IFRAME, type = Special.class),
  @NodeConfig (name = Name.IMG,  end  = Tag.FORBIDDEN, type = Special.class),
  @NodeConfig (name = Name.INPUT, end  = Tag.FORBIDDEN, type = Formctrl.class),
  @NodeConfig (name = Name.INS, type = Default.class),
  @NodeConfig (name = Name.ISINDEX, end  = Tag.FORBIDDEN, type = Block.class),

  @NodeConfig (name = Name.KBD, type = Phrase.class),

  @NodeConfig (name = Name.LABEL, type = Formctrl.class),
  @NodeConfig (name = Name.LEGEND, parent = {Name.FIELDSET} , type = Default.class),
  @NodeConfig (name = Name.LI, end = Tag.OPTIONAL, type = Flow.class, end_names = {Name.LI}),
  @NodeConfig (name = Name.LINK, end  = Tag.FORBIDDEN, parent = {Name.HEAD}, type = HeadMisc.class), 

  @NodeConfig (name = Name.MAP, type = Special.class),
  @NodeConfig (name = Name.MARQUEE, type = Block.class),
  @NodeConfig (name = Name.MENU, type = List.class),
  @NodeConfig (name = Name.META, end  = Tag.FORBIDDEN, parent = {Name.HEAD}, type = HeadMisc.class), 

  @NodeConfig (name = Name.NOBR, type = Block.class),
  @NodeConfig (name = Name.NOFRAMES, parent = {Name.FRAMESET},
		  start = Tag.REQUIRED, end = Tag.REQUIRED,
		  children = {Name.BODY}, type = Block.class),
  @NodeConfig (name = Name.NOSCRIPT, type = Block.class),

  @NodeConfig (name = Name.OBJECT, type = Special.class),
  @NodeConfig (name = Name.OL, type = List.class),
  @NodeConfig (name = Name.OPTGROUP, 
      parent = {Name.SELECT}, children = {Name.OPTION},type = Table.class, move = MoveType.REMOVE
  ),
  @NodeConfig (name = Name.OPTION,
      end = Tag.OPTIONAL, parent = {Name.SELECT, Name.OPTGROUP}, type = Default.class      
  ),

  @NodeConfig (name = Name.P, 
      end = Tag.OPTIONAL, type = Block.class, end_types = {Block.class, Heading.class}
  ),
  @NodeConfig (name = Name.PARAM, 
      end  = Tag.FORBIDDEN, parent = {Name.OBJECT}, type = Default.class
  ),
  @NodeConfig (name = Name.PRE, type = Preformatted.class),

  @NodeConfig (name = Name.Q, type = Special.class),

  @NodeConfig (name = Name.S, type = Fontstyle.class),
  @NodeConfig (name = Name.SAMP, type = Phrase.class),
  @NodeConfig (name = Name.SCRIPT,type = Special.class),
  @NodeConfig (name = Name.SELECT, children = {Name.OPTION, Name.OPTGROUP}, 
      type = Formctrl.class, move = MoveType.REMOVE //block=true
  ),
  @NodeConfig (name = Name.SMALL, type = Fontstyle.class),
  @NodeConfig (name = Name.SPAN, type = Special.class),
  @NodeConfig (name = Name.STRIKE, type = Fontstyle.class),
  @NodeConfig (name = Name.STRONG, type = Phrase.class),
  @NodeConfig (name = Name.STYLE, type = Special.class ),
  @NodeConfig (name = Name.SUB, type = Special.class),
  @NodeConfig (name = Name.SUP, type = Special.class),

  @NodeConfig (name = Name.TABLE, 
      children={Name.TR, Name.TBODY, Name.THEAD, Name.TFOOT, Name.CAPTION, Name.COLGROUP},
      type = Block.class, block = true, move = MoveType.INSERT
  ),
  @NodeConfig (name = Name.TBODY, 
      start = Tag.OPTIONAL, end = Tag.OPTIONAL, parent = {Name.TABLE} , children = {Name.TR},
      type = Table.class, move = MoveType.INSERT
  ),
  @NodeConfig (name = Name.TD, end = Tag.OPTIONAL, parent = {Name.TR}, type = Table.class),
  @NodeConfig (name = Name.TEXTAREA, type = Formctrl.class),
  @NodeConfig (name = Name.TFOOT, 
      end = Tag.OPTIONAL, parent = {Name.TABLE}, children = {Name.TR},
      type = Table.class, move = MoveType.INSERT
  ),
  @NodeConfig (name = Name.TH, end = Tag.OPTIONAL, parent = {Name.TR}, type = Table.class),
  @NodeConfig (name = Name.THEAD, 
      end = Tag.OPTIONAL, parent = {Name.TABLE}, children = {Name.TR}, 
      type = Table.class, move = MoveType.INSERT
  ),
  @NodeConfig (name = Name.TITLE, move = MoveType.HEADER, type = HeadContent.class ),
  @NodeConfig (name = Name.TR, 
      end = Tag.OPTIONAL,  parent={Name.TABLE, Name.TBODY, Name.THEAD, Name.TFOOT}, 
      children = {Name.TD, Name.TH}, type = Table.class, move = MoveType.INSERT
  ),
  @NodeConfig (name = Name.TT, type = Fontstyle.class),

  @NodeConfig (name = Name.U, type = Fontstyle.class),
  @NodeConfig (name = Name.UL, type = List.class),

  @NodeConfig (name = Name.VAR, type = Phrase.class),

  @NodeConfig (name = Name.CONTENT, end = Tag.FORBIDDEN , type = Default.class),
  @NodeConfig (name = Name.CODE_CONTENT, end = Tag.FORBIDDEN , type = Default.class),
  @NodeConfig (name = Name.COMMENT, end  = Tag.FORBIDDEN, type = Default.class),
  @NodeConfig (name = Name.DOCTYPE, end  = Tag.FORBIDDEN, type = Default.class),
  @NodeConfig (name = Name.UNKNOWN, end = Tag.FORBIDDEN , type = Default.class)
})
public class HTML {

  public static SoftReference<NodeConfig[]> refConfig ;
  private final static HashMap<Name, NodeConfig> mapConfig = new HashMap<Name, NodeConfig>();
  
  private final static HashMap<String, Name> mapNames = new HashMap<String, Name>();
  
  static {
    Name [] names = Name.values();
    for(int i = 0; i < names.length; i++) {
      mapNames.put(names[i].name().intern(), names[i]);
    }
  }
  
  static public Name getName(String value){
    return mapNames.get(value);
//    Name name = null;
    /*try{
      name = Name.valueOf(value);
    }catch (Exception e) {
      return null;
    }*/
//    return name;
  }
  
  static public NodeConfig getConfig(String value){
    try {
      return getConfig(getName(value));    
    } catch (Exception e) {
      return null;
    }
  }

  static public NodeConfig getConfig(Name key) {
    NodeConfig config  = mapConfig.get(key);
    if(config != null) return config;
   
    if(refConfig == null || refConfig.get() == null ) loadNodeConfigs();
    NodeConfig [] configs = refConfig.get();
    int low = 0;
    int high = configs.length-1;

    while (low <= high) {
      int mid = (low + high) >> 1;
      config = configs[mid];
      int cmp = config.name().compareTo(key); 
  
      if (cmp < 0) low = mid + 1;
      else if (cmp > 0) high = mid - 1;
      else {
        config = configs[mid];
        mapConfig.put(key, config);
        return config; 
      }
    }
    return null;  
  }
  
  private static void loadNodeConfigs(){
    NodeConfigs nodeConfigs = HTML.class.getAnnotation(NodeConfigs.class);
    NodeConfig [] configs = nodeConfigs.value();
    Arrays.sort(configs, new Comparator<NodeConfig>() {
      public int compare(NodeConfig c1, NodeConfig c2){
        return c1.name().compareTo(c2.name());
      }
    });
    refConfig = new SoftReference<NodeConfig[]>(configs);
  }
  
  static boolean isChild(HTMLNode parent, NodeConfig config){
    if(parent.getConfig().children().length < 1 
        && parent.getConfig().children_types().length < 1 ) return true;
    if(parent.getConfig().end() == Tag.FORBIDDEN) return false;
    if(config.hidden()) return true;
    Name [] names = parent.getConfig().children();
    for(Name name : names){
      if(config.name() == name) return true;
    }
    Class<?> [] children  = parent.getConfig().children_types();
    if(children.length < 1) return false;
    NodeImpl node = new NodeImpl(config.name().toString().toCharArray(), config.name(), TypeToken.TAG);
    for(Class<?> clazz : children){
      if(clazz.isInstance(node)) return true;
    }
    return false;
  }

  static boolean isEndType(HTMLNode node, NodeConfig config){
    Class<?> [] classes = config.end_types();
    for(Class<?> clazz : classes){
      if(clazz != node.getConfig().type()) continue;    
      return true;
    }
    Name [] names = config.end_names();
    for(Name name : names){
      if(node.getConfig().name() != name) continue;
      return true;
    }
    return false;
  }

 }
