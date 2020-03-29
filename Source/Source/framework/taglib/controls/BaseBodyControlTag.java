
package com.i2c.component.framework.taglib.controls;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.html.FormTag;
import java.util.*;
import com.i2c.component.util.ComponentConstants;

public abstract class BaseBodyControlTag extends BodyTagSupport
{


    public BaseBodyControlTag()
    {
        log = LogFactory.getLog(getClass());
        name = null;
    }

    public void setPageContext(PageContext pc)
    {
        super.setPageContext(pc);
    }


protected String id;
    public final void setId(String newId)
    {
        super.setId(newId);
        this.id=newId;
    }
protected String property;
    public final void setProperty(String property)
    {
        this.property=property;
    }
protected String width;
    public final void setWidth(String newWidth)
    {
    	this.width = newWidth;

    }
protected String height;
    public final void setHeight(String newHeight)
    {
        this.height =newHeight;
    }
protected String border;
    public final void setBorder(String newBorder)
    {
    	this.border=newBorder;

    }
protected String tabIndex;
    public void setTabindex(String tabIndex)
    {
		this.tabIndex = tabIndex;
    }

    public final void setName(String name)
    {
        this.name = name;

    }
protected String style;
    public final void setStyle(String style)
    {
        this.style=style;
    }
protected String styleId;
    public final void setStyleId(String styleId)
    {
        this.styleId=styleId;
    }
protected String styleClass;
    public final void setStyleClass(String styleClass)
    {
        this.styleClass = styleClass;
    }
protected String disabled;
    public final void setDisabled(String disabled)
        throws JspException
    {
        this.disabled = disabled;
    }
/*
    public final void setScope(String scope)
        throws JspException
    {
        try
        {
            this.scope = HttpScope.parse(scope);
        }
        catch(InvalidEnumType iet)
        {
            throw new JspException(iet.getMessage());
        }
    }

    public HttpScope getHttpScope()
    {
        return scope;
    }
*/
    public String getName()
    {
        return name;
    }
protected String action;
    protected  void setAction(String action)
    {
        this.action =action;
    }
protected String shaddow;
    public final void setShaddow(String shaddow)
        throws JspException
    {
        this.shaddow = shaddow;
    }
    public final String getFormAction()
    {
        FormTag strutsForm = (FormTag)TagSupport.findAncestorWithClass(this, org.apache.struts.taglib.html.FormTag.class);
        if(strutsForm != null)
            return strutsForm.getAction();
        else
            return null;
    }
protected String formElement;
    public final void setFormElement(String flag)
        throws JspException
    {
        this.formElement = flag;
    }

/*
  public final void createPainter(Control ctrl)
    {
        painter = PainterFactory.createPainter(pageContext, ctrl);
        if(painter == null)
            log.warn("No painter for Tag " + getClass().getName());
    }

    public final void beginPaint()
        throws JspException
    {
        if(painter == null)
            return;
        javax.servlet.jsp.JspWriter out = pageContext.getOut();
        try
        {
            painter.beginPaint(out);
        }
        catch(Throwable t)
        {
            log.error("Error paintBegin() Tag " + getClass().getName(), t);
            throw new JspException(t.getMessage());
        }
    }

    public final void endPaint()
        throws JspException
    {
        if(painter == null)
            return;
        javax.servlet.jsp.JspWriter out = pageContext.getOut();
        try
        {
            painter.endPaint(out);
        }
        catch(Throwable t)
        {
            log.error("Error paintBegin() Tag " + getClass().getName(), t);
            throw new JspException(t.getMessage());
        }
    }
*/
protected String hiddenRowKey;
public final void setHiddenRowKey(String hiddenRowKey) {
	this.hiddenRowKey = hiddenRowKey;
}


protected String onabort;
    public final void setOnabort(String handler)
    {
		this.onabort = handler;
    }
protected String onActivate;
    public final void setOnactivate(String handler)
    {
		onActivate= handler;
    }
protected String onAfterPrint;
    public final void setOnafterprint(String handler)
    {
		onAfterPrint = handler;
    }
protected String onAfterUpdate;
    public final void setOnafterupdate(String handler)
    {
		onAfterUpdate = handler;
    }
protected String onbeforeactivate;
    public final void setOnbeforeactivate(String handler)
    {
		onbeforeactivate = handler;
    }
protected String onBeforecopy;
    public final void setOnbeforecopy(String handler)
    {
		onBeforecopy = handler;
    }
protected String onBeforeCut;
    public final void setOnbeforecut(String handler)
    {
		onBeforeCut = handler;
    }
protected String onbeforedeactivate;
    public final void setOnbeforedeactivate(String handler)
    {
		onbeforedeactivate = handler;
    }
protected String onbeforeeditfocus;
    public final void setOnbeforeeditfocus(String handler)
    {
		onbeforeeditfocus = handler;
    }
protected String onbeforepaste;
    public final void setOnbeforepaste(String handler)
    {
		onbeforepaste = handler;
    }
 protected String onbeforeprint;
    public final void setOnbeforeprint(String handler)
    {
		onbeforeprint = handler;
    }
protected String onbeforeunload;
    public final void setOnbeforeunload(String handler)
    {
		onbeforeunload =handler;
    }
protected String onnbeforeupdate;
    public final void setOnbeforeupdate(String handler)
    {
		onnbeforeupdate = handler;
    }
protected String onblur;
    public final void setOnblur(String handler)
    {
		onblur = handler;
    }
protected String onbounce;
    public final void setOnbounce(String handler)
    {
		onbounce = handler;
    }
protected String oncellchange;
    public final void setOncellchange(String handler)
    {
		oncellchange = handler;
    }
protected String onchange;
    public final void setOnchange(String handler)
    {
		onchange = handler;
    }
protected String onclick;
    public final void setOnclick(String handler)
    {
		onclick = handler;
    }
protected String oncontextmenu;
    public final void setOncontextmenu(String handler)
    {
		oncontextmenu= handler;
    }
protected String oncontrolselect;
    public final void setOncontrolselect(String handler)
    {
		oncontrolselect = handler;
    }
protected String oncopy;
    public final void setOncopy(String handler)
    {
		oncopy= handler;
    }
protected String oncut;
    public final void setOncut(String handler)
    {
		oncut = handler;
    }
protected String ondataavailable;
    public final void setOndataavailable(String handler)
    {
		ondataavailable = handler;
    }
protected String ondatasetchanged;
    public final void setOndatasetchanged(String handler)
    {
		ondatasetchanged = handler;
    }
protected String ondatasetcomplete;
    public final void setOndatasetcomplete(String handler)
    {
		ondataavailable =  handler;
    }
protected String ondblclick;
    public final void setOndblclick(String handler)
    {
		ondblclick = handler;
    }
protected String ondeactivate;
    public final void setOndeactivate(String handler)
    {
		ondeactivate = handler;
    }
protected String ondrag;
    public final void setOndrag(String handler)
    {
		ondrag = handler;
    }
protected String ondragend;
    public final void setOndragend(String handler)
    {
		ondragend = handler;
    }
protected String ondragenter;
    public final void setOndragenter(String handler)
    {
		ondragenter = handler;
    }
protected String ondragleave;
    public final void setOndragleave(String handler)
    {
		ondragleave = handler;
    }
protected String ondragover;
    public final void setOndragover(String handler)
    {
		ondragleave = handler;
    }
protected String ondragstart;
    public final void setOndragstart(String handler)
    {
		ondragstart = handler;
    }
protected String ondrop;
    public final void setOndrop(String handler)
    {
		ondrop = handler;
    }
protected String onerror;
    public final void setOnerror(String handler)
    {
		onerror = handler;
    }
protected String onerrorupdate;
    public final void setOnerrorupdate(String handler)
    {
		onerrorupdate = handler;
    }
protected String onfilterchange;
    public final void setOnfilterchange(String handler)
    {
		onfilterchange = handler;
    }
protected String onfinish;
    public final void setOnfinish(String handler)
    {
		onfinish = handler;
    }
protected String onfocus;
    public final void setOnfocus(String handler)
    {
		onfocus = handler;
    }
protected String onfocusin;
    public final void setOnfocusin(String handler)
    {
		onfocusin = handler;
    }
protected String onfoucsout;
    public final void setOnfocusout(String handler)
    {
		onfoucsout = handler;
    }
protected String onhelp;
    public final void setOnhelp(String handler)
    {
		onhelp = handler;
    }
protected String onkeydown;
    public final void setOnkeydown(String handler)
    {
		onkeydown = handler;
    }
protected String onkeypress;
    public final void setOnkeypress(String handler)
    {
		onkeypress=handler;
    }
protected String onkeyup;
    public final void setOnkeyup(String handler)
    {
		onkeyup = handler;
    }
protected String onlayoutcomplete;
    public final void setOnlayoutcomplete(String handler)
    {
		onlayoutcomplete = handler;
    }
protected String onload;
    public final void setOnload(String handler)
    {
		onload = handler;
    }
protected String onlosecapture;
    public final void setOnlosecapture(String handler)
    {
		onlosecapture = handler;
    }
protected String onmousedown;
    public final void setOnmousedown(String handler)
    {
		onmousedown = handler;
    }
protected String onmouseenter;
    public final void setOnmouseenter(String handler)
    {
		onmouseenter = handler;
    }
protected String onmouseleave;
    public final void setOnmouseleave(String handler)
    {
		onmouseleave = handler;
    }
protected String onmousemove;
    public final void setOnmousemove(String handler)
    {
		onmousemove = handler;
    }
protected String onmouseout;
    public final void setOnmouseout(String handler)
    {
		onmouseout = handler;
    }
protected String onmouseover;
    public final void setOnmouseover(String handler)
    {
		onmouseover = handler;
    }
protected String onmouseup;
    public final void setOnmouseup(String handler)
    {
		onmouseup = handler;
    }
protected String onmousewheel;
    public final void setOnmousewheel(String handler)
    {
		onmousewheel = handler;
    }
protected String onmove;
    public final void setOnmove(String handler)
    {
		onmove = handler;
    }
protected String onmoveend;
    public final void setOnmoveend(String handler)
    {
		onmoveend = handler;
    }
protected String onmovestart;
    public final void setOnmovestart(String handler)
    {
		onmovestart = handler;
    }
protected String onpaste;
    public final void setOnpaste(String handler)
    {
		onpaste = handler;
    }
protected String onpropertychange;
    public final void setOnpropertychange(String handler)
    {
		onpropertychange = handler;
    }
protected String onreadystatechange;
    public final void setOnreadystatechange(String handler)
    {
		onreadystatechange = handler;
    }
protected String onreset;
    public final void setOnreset(String handler)
    {
		onreset = handler;
    }
protected String onresize;
    public final void setOnresize(String handler)
    {
		onresize = handler;
    }
protected String onresizeend;
    public final void setOnresizeend(String handler)
    {
		onresizeend = handler;
    }
protected String onresizestart;
    public final void setOnresizestart(String handler)
    {
		onresizestart = handler;
    }
protected String onrowenter;
    public final void setOnrowenter(String handler)
    {
		onrowenter = handler;
    }
protected String onrowexit;
    public final void setOnrowexit(String handler)
    {
		onrowexit = handler;
    }
protected String onrowsdelete;
    public final void setOnrowsdelete(String handler)
    {
		onrowsdelete = handler;
    }
protected String onrowsinserted;
    public final void setOnrowsinserted(String handler)
    {
		onrowsinserted = handler;
    }
protected String onscroll;
    public final void setOnscroll(String handler)
    {
		onscroll = handler;
    }
protected String onselect;
    public final void setOnselect(String handler)
    {
		onselect = handler;
    }
protected String onselectionchange;
    public final void setOnselectionchange(String handler)
    {
		onselectionchange = handler;
    }

protected String onSelectstart;
    public final void setOnselectstart(String handler)
    {
		onSelectstart= handler;
    }
 protected String onStart;
    public final void setOnstart(String handler)
    {
        onStart = handler;
    }
protected String onStop;
    public final void setOnstop(String handler)
    {
		onStop= handler;
    }
protected String  onSubmit;
    public final void setOnsubmit(String handler)
    {
        this.onSubmit = handler;
    }
    protected String onunload;
    public final void setOnunload(String handler)
    {
		onunload = handler;
    }
    /**
     * @param valueName is the name of class(Bean) which contains data and is saved in the bean
     * @param value is the name of the property(Function) we are going to call to get the data from the bean
     * @return
     * @throws Exception
     */
	public Object getOBJValue(String valueContainerName , String value) throws Exception{
		Object obj ="";
		if(valueContainerName!=null && value!=null){
			try{
				obj= this.pageContext.getSession().getAttribute(valueContainerName);

				if(obj!=null){
					if(obj instanceof Hashtable){
						Hashtable htable= (Hashtable)obj;
						obj=htable.get(value);
					}
				}
			}catch(Exception exp)
			{
				exp.printStackTrace();
			}
		}
		if(obj==null) obj="";

		obj = handleSpecialCharacters(obj);
		return obj;
	}

	/**
	 *
 	 * @param valueName is the name of Collection which contains further objects containg data
	 * @param value is the name of the property(Function) we are going to call to get the data from the bean
     * @param mRows_And_Column is used to determin if there are multiple rows and columns are there.
	 * @return
	 * @throws Exception
	 */

	public java.util.List getList(String listName ) throws Exception{
		Object obj =null;
		List list=null;
		if(listName!=null ){
			try{
				//System.out.println("listName"+listName);
			obj= this.pageContext.getSession().getAttribute(listName);
				//System.out.println("obj"+obj);
			if(obj instanceof ArrayList) {
				list = (ArrayList)obj;
				this.pageContext.getSession().removeAttribute(listName);
			}
			}catch(Exception exp)
			{
				exp.printStackTrace();
			}
		}
		return list;

	}
    protected Log log;
    protected String name;

	/**
	 * @description 	This method is used to stuff HTML codes for special characters into the value attribute of Data Control(s).
	 * @param value 	Object - castable to type String.
	 * @return	String	The updated string containing special character codes.
	 * @author	edwin yaqub - May 30, 2005.
	 */
	protected String handleSpecialCharacters(Object value)
	{
		StringBuffer stringBuffer = new StringBuffer((String)value);
		//System.out.println("stringBuffer.length() = "+stringBuffer.length());

		for(int i=0; i<stringBuffer.length(); i++)
		{
			if(stringBuffer.charAt(i) == '\'')
			{
			 	//Stuff &#39; against ' character inside value
			 	//System.out.println("************************ Got ' character");
				stringBuffer.replace(i,i+1, ComponentConstants.singleQuotHtmlCode);
			}
			else if(stringBuffer.charAt(i) == '"')
			{
				//Stuff &quot; against " character inside value
				//System.out.println("************************ Got \" character");
				stringBuffer.replace(i,i+1,ComponentConstants.doubleQuotHtmlCode);
			}
			else if(stringBuffer.charAt(i) == '&')
			{
				//Stuff &amp; against & character inside value
				//System.out.println("************************ Got & character");
				stringBuffer.replace(i,i+1, ComponentConstants.ampersandHtmlCode);
			}
			else if(stringBuffer.charAt(i) == '<')
			{
				//Stuff &lt; against < character inside value
				//System.out.println("************************ Got < character");
				stringBuffer.replace(i,i+1,ComponentConstants.lessThanHtmlCode);
			}
			else if(stringBuffer.charAt(i) == '>')
			{
				//Stuff &gt; against > character inside value
				//System.out.println("************************ Got > character");
				stringBuffer.replace(i,i+1,ComponentConstants.greaterThanHtmlCode);
			}
		}

		return stringBuffer.toString();
	}


}