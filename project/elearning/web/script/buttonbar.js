ulm_ie=window.showHelp;ulm_opera=window.opera;ulm_strict=((ulm_ie || ulm_opera)&&(document.compatMode=="CSS1Compat"));ulm_mac=navigator.userAgent.indexOf("Mac")+1;sd_add="";
if(ulm_mac && ulm_ie && document.doctype)
{
	tval=document.doctype.name.toLowerCase();
		if((tval.indexOf("dtd")>-1)&&((tval.indexOf("http")>-1)||(tval.indexOf("xhtml")>-1)))
				ulm_strict=1;}b0=document.getElementsByTagName("UL");

		for(mi=0;mi<b0.length;mi++)
				{
					if(b1=b0[mi].id)
						{
							if(b1.indexOf("buttonbar")>-1){b1=b1.substring(9);
									b2=new window["buttonbardata"+b1];
	
							b3=b0[mi].childNodes;this.counter=0;
							for(this.li=0;this.li<b3.length;this.li++)
							{
							if(b3[this.li].tagName=="LI")
									{
									this.bc="b4"+b1+"_"+this.counter;
									b3[this.li].id=this.bc;
									this.ac="b5"+b1+"_"+this.counter;
									b3[this.li].firstChild.id=this.ac;
									this.counter++;
									}
							 }
							 b8(b1,b2);
							 b0[mi].parentNode.style.display="block";
						 }
			    	 }
	};
	
	
	function b8(id,b2)
	{
		b7="#buttonbar"+id;
		b9=b2.item_padding.split(",");
		sd="<style type='text/css'>";
		b6="auto";
		if(b2.is_horizontal)
		{
			b11=0;di=0;
			while(document.getElementById("b4"+id+"_"+di))
			{
				b10=b12(b2,"item_width",di);
				b11+=b10;
				sd+="#b4"+id+"_"+di+" {float:left;width:"+b10+"px;}";
				if(ulm_ie && ulm_strict)
						sd+="#b5"+id+"_"+di+" {width:"+(b10-parseInt(b9[1])-parseInt(b9[3]))+"px;}";
				di++;
			}
			
			if((ulm_ie || ulm_opera)&& !ulm_strict)
				{
				if(b2.container_border_style.toLowerCase()!="none")
					b11+=(parseInt(b2.container_border_width)*2);
				}document.getElementById("buttonbar"+id).style.width=b11+"px";
			}
			else b6=b2.item_width+"px";
			sd+=b7+",#buttonbar"+id+" a{margin:0;list-style:none;width:"+b6+";}";
			sd+=b7+" {border-width:"+b2.container_border_width+";border-style:"+b2.container_border_style+";"+b2.container_styles+"padding:0;}";
			sd+=b7+","+b7+" li {font-size:1px;}";
			ulp="";

			if(ulm_ie)
				{
				if(!ulm_strict)
					ulp="width:100%;";
				else sd+=b7+"a{width:100%;}";
				sd+=b7+" a:hover{"+b2.item_hover_styles+"}";
				}
				else sd+=b7+" li:hover > a {"+b2.item_hover_styles+"}";
			if(b2.item_active_styles)sd+=b7+" a:active,"+b7+" a:focus{"+b2.item_active_styles+"}";
			sd+=b7+" a{display:block;"+ulp+" "+b2.item_styles+"padding-top:"+b9[0]+";padding-right:"+b9[1]+";padding-bottom:"+b9[2]+";padding-left:"+b9[3]+";}";
			document.write(sd+sd_add+"</style>");if((ulm_ie)&&(ulm_mac)&&(b2.is_horizontal)){b13=0;
			if(!ulm_strict)b13=parseInt(b2.container_border_width)*2;
			window["buttonbar"+id].style.height=(window["b5"+id+"_0"].offsetHeight+b13)+"px";
			}
			};
			
			function b12(b2,lookfor,id)
			{
				if(b2[lookfor+id]!=null)return b2[lookfor+id];
				else  if(b2[lookfor]!=null)return b2[lookfor];
				else return null;
			}


function buttonbardata0()
{
	this.is_horizontal = true
	this.item_width = 76

	this.container_border_width = "0px"
	this.container_border_style = "none"
	this.container_styles = "border-color:#000000;"


	this.item_padding = "1px,0px,0px,0px"




	this.item_styles =            "text-align:center;				\
                                       text-decoration:none;				\
                                       height:17;					\
                                       font-weight:normal;				\
                                       font-family:Arial;				\
                                       font-size:12px;					\
                                       background-image:url(images/sample1_default.gif);\
                                       color:#FFFFFF;					\
                                       border-style:none;				\
                                       border-width:0px;				"




	this.item_hover_styles =      "text-align:center;				\
                                       text-decoration:none;				\
                                       font-weight:normal;				\
                                       font-family:Arial;				\
                                       font-size:12px;					\
                                       background-image:url(images/sample1_roll.gif); 	\
                                       color:#FFFFFF;					\
                                       border-style:none;				\
                                       border-width:0px;				"


}
