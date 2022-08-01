var globalapikey = '';
var requestcontentdata = '';
var docurl = '';
var daturl = '';

function gettimekey(v) {  
    var x = v;
    var sel = x.selectionStart;
    var csel = x.value.length - sel; 

    var cur = x.value;    
    cur = cur.replace(/:/g, "");
/*
    var dec = "";
    if (cur.indexOf(".")>=0 ){
        dec = cur.substr(cur.indexOf("."));
        cur = cur.substr(0, cur.indexOf("."));        
    }else{
    }  
    
    
    if (cur.length==0){
        cur = "0000"+cur;
    }else if (cur.length==1){
        cur = "000"+cur;
    }else if (cur.length==2){
        cur = "00"+cur;
    }else if (cur.length==3){
        cur = "0"+cur;
    }else if (cur.length==4){  
        cur = cur;
        
    }
    
    */
    
    cur = cur.replace(/\B(?=(\d{2})+(?!\d))/g, ":");
    //cur = cur.concat(dec);
    
    if (cur.indexOf(":")>=0 &&  cur.length>=5){
        var m = cur.substr(cur.indexOf(":")+1);
        var h = cur.substr(0, cur.indexOf(":"));   
        if(parseInt(h)>=24 ){
            h = "00";
        }
        if(parseInt(m)>=60){
            m = "00";
        }
        cur = h +":"+m;
    }  



    csel = cur.length - csel; 

    x.value =cur;
    x.selectionStart = csel;
    x.selectionEnd = csel;
}
function gettimemask(evt, none) {  
    if(evt.keyCode === 13){
        
    }
    evt = (evt) ? evt : window.event;   
       console.log(this);
         console.log(evt);
    if(evt.charCode === 0){
        return true;
    }
    var charValue = String.fromCharCode(evt.charCode);
    var r = new RegExp("[0123456789]","g");       
    var valid = r.test(charValue);
    
    if (!valid) {
       return false;
    }else{
        if(evt.target.value.length>4){
            return false;
        }        
        return true;
    }
   //hh:mm [0-23]:[0-59]
}

function getnumbercurr(cur){    
    cur = cur.replace(/,/g, "");
    return cur;
}
function currencykeyup(v){
    var x = v;
    var sel = x.selectionStart;
    var csel = x.value.length - sel; 
 
    var cur = x.value;    
    cur = cur.replace(/,/g, "");
    
    var dec = "";
    if (cur.indexOf(".")>=0 ){
        dec = cur.substr(cur.indexOf("."));
        cur = cur.substr(0, cur.indexOf("."));        
    }else{
    }    
    cur = cur.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    cur = cur.concat(dec);

    csel = cur.length - csel; 

    x.value =cur;
    x.selectionStart = csel;
    x.selectionEnd = csel;
    
}
function fromchild(dataz, param1, param2){
    alert (dataz);
   
}
function fromchildresult(frmcode, respcode, resultdata){
    var resultz = {};
    resultz.data = resultdata;
    var reqrescode = {};
    reqrescode.responsecode=respcode;
    reqrescode.requestcode='CHILD';
    sendactionpost(frmcode, frmcode,'nikita-child-listener','result',JSON.stringify(resultz),JSON.stringify(reqrescode));
}

function showdialog(formidz, titlez, messagez, reqcodez, buttonz){
    var idform = 'nikita-form-dialog';  
       
    var requestresult = '';
    if (buttonz.requestresult){
        requestresult=buttonz.requestresult;//jsnon dlm html escape
    }else{
        requestresult = '{}';
    }
    var elm = document.getElementById(idform);
    if (elm==null){
        var element = document.createElement('div');
        element.id = idform;
        element.setAttribute('title',titlez);
        element.setAttribute('reqcode',reqcodez);
        element.setAttribute('formid',formidz);
        element.setAttribute('rescode','close');
        element.setAttribute('class','nikita-form-dialog');
        element.innerHTML = '<div style="display:none" id="nikita-form-dialog-tag">'.concat(requestresult).concat('</div><p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>').concat(messagez).concat('</p>');
        document.body.appendChild(element);
    }else{
        elm.setAttribute('title',titlez);
        elm.setAttribute('reqcode',reqcodez);
        elm.setAttribute('formid',formidz);
        elm.setAttribute('rescode','close');
        elm.innerHTML = '<div style="display:none" id="nikita-form-dialog-tag">'.concat(requestresult).concat('</div><p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>').concat(messagez).concat('</p>');  
    }
  
    var buttons = {};
    if (!buttonz.button1){}else{
        buttons[buttonz.button1] =  function() {
            $( this ).attr('rescode','button1');
            $( this ).dialog( "close" );                 
        }
    }
    if (!buttonz.button2){}else{
        buttons[buttonz.button2] =  function() {
            $( this ).attr('rescode','button2');
            $( this ).dialog( "close" );                 
        }
    }
    if (!buttonz.button3){}else{
        buttons[buttonz.button3] =  function() {
            $( this ).attr('rescode','button3');
            $( this ).dialog( "close" );                 
        }
    }
    $( '#'.concat(idform) ).dialog({
        resizable: false,
        modal: true,
        buttons: buttons,
        title:titlez,
        close: function() {            
            ontop();
        },   
        beforeClose: function() {
            var reqcode = $( this ).attr('reqcode');
            var frmcode = $( this ).attr('formid');
            var rescode = $( this ).attr('rescode');
            var resultz = $( "#nikita-form-dialog-tag" ).html(); 
            var reqrescode = {};
            reqrescode.responsecode=rescode;
            reqrescode.requestcode=reqcode;
            //'nikita-form-dialog'
            if(rescode!== "close"){                
                sendactionpost(frmcode, frmcode,'nikita-dialog-listener','result',resultz,JSON.stringify(reqrescode));
            }
        }
    });
    ontop();
}

function showform(formidz, textz, modalz, style, closeconsume, fid, fname){
    var w = window.innerWidth
    || document.documentElement.clientWidth
    || document.body.clientWidth;

    var h = window.innerHeight
    || document.documentElement.clientHeight
    || document.body.clientHeight;
    
    var frm = "#";   
    var height=h-120;//480
    var width=w-120;//800
     
    
    try {
        if (dataforms[fname]){            
        }else{          
            dataforms[fname] = {};
        }
    }catch (e){}
    
    
    //alert ( JSON.stringify(style));
    if(style.width){
        width=style.width;
    }
    if(style.height){
        height=style.height;
    }
    if(style.maximize){
        //width="99%";
        //height= $( window ).height() ;
    }
    var modalable = false;
    if(modalz.indexOf('true')>-1){
        modalable = true;
    }
    
    var elm = document.getElementById(formidz);
    if (elm==null){
    }else{
        elm.setAttribute('consume', closeconsume);    
        elm.setAttribute('fid', fid); 
        elm.setAttribute('fnt',  new Date().getTime());       
    }
    
    try {
        dialog = $( frm.concat(formidz) ).dialog({
        title:textz,
        height: height,
        width:  width,
        resizable: style.resizable =="true",
        draggable: style.draggable =="true",
        closeOnEscape : style.maximizable =="true",
        modal: modalable,
        close: function() {            
            ontop();
            $(this).dialogExtend().remove(); 
        },      
        beforeClose: function(event, ui) {   
            $( this ).removeClass("nikita-maximize");
            var consume = $( this ).attr('consume');
            var fid = $( this ).attr('fid');
            if(consume.indexOf('close')>-1){
                //sendaction(sdata.id, sdata.id,'close',sdata.data, sdata.reqrescode);
                sendactionpost(fid, fid,'','close');
                return false;//no close, nunggu server close
            }
        },open: function (event, ui) {
            if(style.overflow){
               $(this).css('overflow', style.overflow);
            }
            
        }
        
        }).dialogExtend({
            "closable" : style.closable=="true" ,
            "closeOnEscape" : style.maximizable =="true",
            "maximizable" : style.maximizable =="true",
            "minimizable" : style.minimizable=="true" && (!modalable) ,
            "collapsable" : false,
            "dblclick" : style.maximizable =="true"?"maximize":false,
            "titlebar" : false,
            "minimizeLocation" : "right",
            "icons" : {
              "close" : "ui-icon-close",
              "maximize" : "ui-icon-arrow-4-diag",
              "minimize" : "ui-icon-minus",
              "collapse" : "ui-icon-triangle-1-s",
              "restore" : "ui-icon-refresh"
            }
        }) ;
        
        
        
        $( "#".concat(formidz) ).parent().append( $(  "#".concat(fid).concat("-busy")  ) );
        $( "#".concat(formidz) ).parent().append( $(  "#".concat(fid).concat("-wait")  ) );
            
        
        
        
    }catch (e){}   

    if(style.maximize){
       try{dialog.dialog('maximize');  }catch (e){}
    }
    
   

    /*
    if(style.maximize){
        $( frm.concat(formidz) ).addClass("nikita-maximize",0);
        $( ".nikita-maximize" ).each(function() {   
            $(this).dialog("option", "resizable", false );
            $(this).dialog("option", "draggable", false );
        });
    } 
    */
}


function showbusy(){
    /*
    var x = $( "#nikita-busy" );
        x.css( "background-color","black" );
        x.css( "opacity", "0.5" );
        x.css( "zIndex", 99999 );  
        x.show();
        */
}
function closebusy(){
     /*
    var x = $( "#nikita-busy" );
         x.css( "zIndex", 1 );  
         x.hide();
     */
}

 
function resizelayout() {
        $( ".nikita-maximize" ).each(function() {   
            //$(this).dialog("option", "height", $( window ).height()-10 );
            //$(this).dialog("option", "position", { my: "center", at: "center", of: window } );           
        });
}
    
function relayout() {
    /*
        $( ".nikitaborderlayout" ).each(function() {   
              $( this ).layout({resize: false, type: 'border',  vgap: 8, hgap: 8});    
        });
        $( ".1nikitahorisontallayout" ).each(function() {   
              $( this ).layout({resize: false, type: 'flexGrid', rows: 1,    vgap: 8, hgap: 8});    
        });   
        $( ".nikitaframelayout" ).each(function() {   
              $( this ).layout({resize: false, type: 'flexGrid', columns: 1,    vgap: 8, hgap: 8});    
        });  
        */
        //alert('relayout');
        
        
        
 
}
function getStyle(className) {
    var classes = document.styleSheets[0].rules || document.styleSheets[0].cssRules;
    for (var x = 0; x < classes.length; x++) {
        if (classes[x].selectorText == className) {
            (classes[x].cssText) ? alert(classes[x].cssText) : alert(classes[x].style.cssText);
        }
    }
}
function viewmandatory(compid, mand){
    try{        
        if (mand) {
            $( '#'+compid  ).css("display","block");
        }else{
            $( '#'+compid  ).css("display","none");  
        }           
    }catch(e){}             
}
function viewvisibility(compid, mand){
    try{        
        if (mand) {
            $( '#'+compid  ).css("display","block");
        }else{
            $( '#'+compid  ).css("display","none");  
        }           
    }catch(e){}             
}

function viewmandatoryerror(compid, err){
    try{        
        if (err) {             
            $( '#'+compid+'_text').addClass('n-error');
        }else{           
            $( '#'+compid+'_text' ).removeClass('n-error');  
        }           
    }catch(e){}  
    
    try{        
        if (err) {             
            $( '#'+compid+'_verr').addClass('n-error');
        }else{           
            $( '#'+compid+'_verr' ).removeClass('n-error');  
        }           
    }catch(e){}  
    try{        
        if (err) {             
            $( '#'+compid+'_combo_text').addClass('n-error');
        }else{           
            $( '#'+compid+'_combo_text' ).removeClass('n-error');  
        }           
    }catch(e){}  
    
}
function closeform(formidz, fname){
 
    if (formidz == '*'){
          dataforms = {};
    }else{          
      var frm = "#"; 
      var elm = document.getElementById(formidz);
      if (elm==null){
          
      }else{
          elm.setAttribute('consume', 'will');   
      }      
      dialog = $( frm.concat(formidz) ).dialog('close');
        delete dataforms[fname];
      }
}
function backform(formidz, fname){
 
    if (formidz == '*'){
         // dataforms = {};
    }else{          
      var frm = "#"; 
      var elm = document.getElementById(formidz);
      if (elm==null){
          
      }else{
          elm.setAttribute('consume', 'will');   
      }      
      dialog = $( frm.concat(formidz) ).dialog('close');
        //delete dataforms[fname];
      }
}
function cleardataform(formidz, fname){
 
    if (formidz == '*'){
         // dataforms = {};
    }else{          
        var frm = "#"; 
        delete dataforms[fname];
      }
}

function sendNikitaEngineActive(formz){
    
}
function first(){
    $( window ).resize(function() {
        var newh = $(window).height() - 1;
        var h = window.innerHeight
        || document.documentElement.clientHeight
        || document.body.clientHeight;
        
        $(".ui-dialog-maximized").each(function() { 
            $( this).dialog( "option", "height", h );
        }) ;
    });
    
}
function stayonpage(){
    window.onbeforeunload = function() { return "Nikita Generator will be close."; };
}

function stayonpageform(){
    window.onbeforeunload = function() { 
        
        return "Nikita Generator will be close."; 
    };
}

function firsta(){
    if (navigator.geolocation) {
        var v = JSON.stringify(position) ;
    }
    
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.onreadystatechange = function() {
        // if (this.readyState == 'complete') { callFunctionFromScript(); }
    }
    script.src = 'data:text/javascript;base64,YWxlcnQoJ2FzYWRhc2QnKTsK';
       //'https://ajax.googleapis.com/ajax/libs/angular_material/0.8.2/angular-material.min.js';
    head.appendChild(script);
    
 
    var scripts = document.getElementsByTagName('script');
    var i = scripts.length;
    while (i--) {
      scripts[i].parentNode.removeChild(scripts[i]);
    }
    
}
var NvarMap = {};
var NvarMapMarker = {};
var NvarMapPath = {};
 
function nmenusmartgrid(){
    /*
    $.contextMenu({
        selector: '.n-menu-smartgrid-action', 
        trigger: 'hover',
        delay: 100,
        callback: function(key, options) {
            var m = "clicked: " + key+$(this).attr('id');
            window.console && console.log(m) || alert(m); 
        },
        items: {
            "edit":  {name: "Edit", icon: "edit"},
            "cut":   {name: "Cut", icon: "cut"},
            "copy":  {name: "Copy", icon: "copy"},
            "paste": {name: "Paste", icon: "paste"nmenusmartgrid},
            "delete":{name: "Delete", icon: "delete"},
            "sep1":  "---------",
            "quit":  {name: "Quit", icon: "quit"}
        }
    });*/
    $(".n-menu-smartgrid-action").each(function() {  
        //$(this).removeClass('n-menu-smartgrid-action');
    }) ;
    $.contextMenu({
        selector: '.n-menu-smartgrid-action', 
        trigger: 'left',
        delay: 100,
        autoHide: true,
        build: function($trigger, e) {
            //var data =  $.parseJSON(  $(this).attr("items")   ) ;
            //console.log( $(e.target).attr("items") );
           // console.log(e);               
            
            var b64item = Base64.decode($(e.target).attr("items") );
            var itmesjson =  $.parseJSON(  b64item  ) ;
            var itemList = {};
            for (var i = 0; i < itmesjson.length; i++){
                itemList[itmesjson[i].key] = itmesjson[i];
                            
            }
            
            //sendactiongrid(formjsid,compjsi,action,'',row);
            // this callback is executed every time the menu is to be shown
            // its results are destroyed every time the menu is hidden
            // e is the original contextmenu event, containing e.pageX and e.pageY (amongst other data)
            return {
                callback: function(key, options) {
                    var m =  ''+key;
                     console.log (  m   );
                    //window.console && console.log(m) || alert(m); 
                   // console.log (  $(this).attr("item-action")   );
                    //console.log ( Base64.decode($(this).attr("item-action") )   );
                    //var decodedString = Base64.decode($(this).html());
                    //sendactiongrid(formjsid,compjsi,action,'',row);
                    var b64item = Base64.decode($(this).attr("item-action") );
                    var actionjson =  $.parseJSON(  b64item  ) ;
                    sendactiongrid(actionjson.formid, actionjson.compid, actionjson.action+m ,'', actionjson.row);
                },
                items: itemList
            };
        }
    });
    
    
}


function init(){
    nmenusmartgrid();
    initResizable();//reisze
    if (!Array.isArray) {
        Array.isArray = function(arg) {
          return Object.prototype.toString.call(arg) === '[object Array]';
        };
     }
     
    $( document ).tooltip();
     
    $(".nprogressbar").each(function() {  
        $( this ).progressbar({
            value: 0,
            change: function() {
               $(  "#".concat($( this ).attr('id')).concat("-text")    ).text( $( this ).progressbar( "value" ) + "%" );
            },
            complete: function() {
              $(  "#".concat($( this ).attr('id')).concat("-text")    ).text( "Complete!" );
            }
        });
        $(this).removeClass('nprogressbar');
    }) 
    
    $(".nsmartgrid").each(function() {  
       // $(this).colResizable();
        $(this).removeClass('nsmartgrid');
    }) ;   
    
   $(".smartgridresizable").each(function() {  
        $(this).resizable({ handles: 'e', grid: [1, 100000] });   
        $(this).removeClass('smartgridresizable');
    }) ;
    
    $(".naccordion").each(function() {  
        var active = $( this ).attr('activate');        
        $(this).accordion({ heightStyle: "content", collapsible: true, active: parseInt(active) }) ;
        $(this).removeClass('naccordion');        
    }) ;
 
    $(".ncollapsibleList").each(function() {  
       collapsibleLists.apply();
       $(this).removeClass('ncollapsibleList');
    }) ;
    
    $(".nTree").each(function() {        
        //$(this).easytree();
        $(this).removeClass('nTree');
    }) ;
    
    $(".ncombolist").each(function() {  
        $(this).dropdownchecklist( { width: 240 , icon: {}, 
        maxDropHeight: 200 } );
        $(this).removeClass('ncombolist');
    }) ;    
    
    $(".nautocomplete").each(function() {    
        var data =  $.parseJSON( $(   '#'.concat($(this ).attr('id').concat('_data'))    ).html()  ) ;
        $(this).autocomplete({
            source: data,
            
            select: function( event, ui ) { 
                $(   '#'.concat($(this ).attr('id'))    ).val( ui.item.value  );
                $(   '#'.concat($(this ).attr('id').concat('_data'))    ).click(); 
            
            }
        });
        $(   '#'.concat($(this ).attr('id').concat('_data'))    ).html(""); 
        $(this).removeClass('nautocomplete');
    }) ;   
    //zomm
    $(".ndocumentzoom").each(function() {  
        //wheelzoom(document.querySelector(  '#'.concat( $(this ).attr('id').concat('_image'))    ));
        wheelzoom(document.querySelector(  '#'.concat( $(this ).attr('id') )    ));
        $(this).removeClass('ndocumentzoom');
    }) ; 
    
    $(".nbutton").each(function() {  
        $(this).button();
        $(this).removeClass('nbutton');
    }) ;
    
    $(".nmenulist").each(function() {  
        $(this).menu();
        $(this).removeClass('nmenulist');
    }) ;
    
    $(".ndatetime").each(function() {  
        var  mindate = $( this ).attr('mindate');
        var  maxdate = $( this ).attr('maxdate');
        var  yrrange = $( this ).attr('yearrange');
        
        
        
        var args = {};
        args.dateFormat = "dd/mm/yy";
        if (mindate){            
            try {
                args.minDate = new Date(parseInt(mindate));
            }catch (e){}
        }
        if (maxdate){  
             try {
                args.maxDate = new Date(parseInt(maxdate));
            }catch (e){}
        }       
        
        if (yrrange){            
            try {
                args.changeYear = true;
                args.yearRange = yrrange;
            }catch (e){}
        }
        
         
        
        $(this).datepicker(args);//{ dateFormat: "dd/mm/yy" }
        $(this).removeClass('ndatetime');
    }) ;
    
    $(".ntab").each(function() {  
        var active = $( this ).attr('activate');
        $(this).tabs({ heightStyle: "content", collapsible: true, active: parseInt(active) }) ;
        $(this).removeClass('ntab');
    }) ;
    
    $(".nformupload").each(function() {     
            $(this).ajaxForm({                
                beforeSend: function(xhr, o) {
                    var idstatus = '#'.concat($( o.form ).attr('id').concat('_status'));
                    var idbar = '#'.concat($( o.form ).attr('id').concat('_bar'));
                    var idpercent = '#'.concat($( o.form ).attr('id').concat('_percent'));
                    
                    var status = $(idstatus);
                    var bar = $(idbar);
                    var percent = $(idpercent);
                    
                    status.empty();
                    var percentVal = '0%';
                    bar.width('220px');
                    percent.html(percentVal);
                    bar.show();
                    percent.show();
                    bar.progressbar({ value: 0   });                     
                }, 
                url:getdocurl(''),
                success : function(data, status, xhr, form) {
                  //  alert(data);
                },
                error : function(xhr, status, error, form) {
                    var idbar = '#'.concat($( form ).attr('id').concat('_bar'));
                    var idpercent = '#'.concat($( form ).attr('id').concat('_percent'));
                    $( idbar).hide();
                    $( idpercent).hide();

                    var reqcode = 'submit';
                    var frmcode = $( form ).attr('fname');
                    var rescode = $( form ).attr('filename');//rescode
                   
                    var reqrescode = {};
                    reqrescode.responsecode=rescode;
                    reqrescode.requestcode=reqcode;
                    
                    var vv = {};
                    vv.error=error;
                    
                    sendactionpost(frmcode, frmcode,'nikita-upload-listener','result',JSON.stringify(vv),JSON.stringify(reqrescode));
                },
                uploadProgress: function(event, position, total, percentComplete, form) {
                    var idbar = '#'.concat($( form ).attr('id').concat('_bar'));
                    var idpercent = '#'.concat($( form ).attr('id').concat('_percent'));
                                       
                    var bar = $(idbar);
                    var percent = $(idpercent);
                                        
                    var percentVal = percentComplete + '%';
                    percent.html(percentVal);
                    
                    bar.progressbar({ value: percentComplete   });
                },
                complete: function(xhr,status,form) {
                    var idbar = '#'.concat($( form ).attr('id').concat('_bar'));
                    var idpercent = '#'.concat($( form ).attr('id').concat('_percent'));
                    $( idbar).hide();
                    $( idpercent).hide();

                    var reqcode = 'submit';
                    var frmcode = $( form ).attr('fname');
                    var rescode = $( form ).attr('filename');//rescode
                    var resultz = {};//$( "#nikita-form-dialog-tag" ).html(); 
                    var reqrescode = {};
                    reqrescode.responsecode=rescode;
                    reqrescode.requestcode=reqcode;
                    
                    //alert(status);
                    //alert(xhr.responseText);
                    sendactionpost(frmcode, frmcode,'nikita-upload-listener','result',xhr.responseText,JSON.stringify(reqrescode));
                     
                    //status.html(xhr.responseText);
                }
            });
            $(this).removeClass('nformupload');
        }) ;
        
    ontop();
    
    $(".nmap").each(function() {  
        try {
            var map;
            var z =12;
            var id ='#'.concat($( this ).attr('id').concat('_text')) ; 
            var datamap = $.parseJSON( $( id ).html() );//[{"lat":""}]             
            var vs = datamap;
 
  
         
 
            var lat = 0;
            var lng = 0;            
            NvarMapMarker[$( this ).attr('id')]= [];
            var path ;
            var bpath = '';
            var pathcolor = '#000000';
            var pathweight=3;
          
            
            
            if (!Array.isArray(datamap)){
                vs = [];
                vs.push(datamap);
            }
            
            
            for (var i = 0; i < vs.length; i++){
                try {
                    lat = parseFloat(vs[i].lat);
                    lng = parseFloat(vs[i].lng);
                    if (vs[i].latitude){ 
                        lat = parseFloat(vs[i].latitude);
                    }
                    if (vs[i].longitude){ 
                        lng = parseFloat(vs[i].longitude);
                    }
                }catch (e){}
                var latlng =  new google.maps.LatLng(lat, lng);
                var markerdata = {};
                var pathdata = {};                
                
                if (i===0){
                    
                    if (vs[i].zoom){
                        z = parseInt(vs[i].zoom);
                    }
                    
                    if (vs[i].pathcolor){   
                        pathcolor=vs[i].pathcolor;
                    } 
                     if (vs[i].pathweight){   
                        pathweight=vs[i].pathweight;
                    } 
                    
                    
                    var mapOptions  = {
                        zoom: z,
                        center: latlng,
                        mapTypeId: google.maps.MapTypeId.ROADMAP
                    };
                    var map = new google.maps.Map(document.getElementById($( this ).attr('id')),  mapOptions);   
                    var marker = new google.maps.Marker({
                        position: latlng,
                        map: map,
                        title: 'Nikita' 
                    });
                    
                    
                    path = new google.maps.Polyline({
                        strokeColor: pathcolor,
                        strokeOpacity: 1.0,
                        strokeWeight: pathweight
                    }); 
                    path.setMap(map);
                    if (vs[i].path){
                        bpath = vs[i].path;                
                    }
                                   
                }
                
                markerdata.position = latlng;
                markerdata.map=map;
                markerdata.title= 'Nikita';
                
                var nicomp = {};
                nicomp.id = '';
                if (vs[i].id){
                    nicomp.id = vs[i].id;
                }                 
                nicomp.compjsid = $( this ).attr('compid');
                nicomp.formjsid = $( '#'+ nicomp.compjsid ).attr('nfid');                
                nicomp.actionjsid ='marker-'+nicomp.id;                  
                
                nicomp.mapid = $( this ).attr('id');
                if (vs[i].info){
                    nicomp.info = vs[i].info;
                }
                markerdata.nikita= nicomp;
                
                
                if (vs[i].icon){   
                     markerdata.icon=vs[i].icon;
                }  
                if (vs[i].title){   
                    markerdata.title=vs[i].title;
                } 
                var marker = new google.maps.Marker(markerdata);
                NvarMapMarker[$( this ).attr('id')].push(marker);
                clickmapmarker(marker, $( this ).attr('id'), '');
                
                
                pathdata.strokeColor = pathcolor;
                pathdata.strokeOpacity= 1.0;
                pathdata.strokeWeight = 3;
                if (vs[i].pathcolor){   
                    pathdata.pathcolor=vs[i].pathcolor;
                } 
                
                var xpath = path.getPath();   
                
                if (bpath=== true || bpath === 'true'){
                    xpath.push(latlng);
                }    
                
                
                
            } 
            NvarMap[$( this ).attr('id')] = map;
            NvarMapPath[$( this ).attr('id')] = path;
            
            setmapmarker($( this ).attr('id'), datamap); 
        }catch (e){    }
         
       
    }) ;//nmap
    
    
    
    execinitload();
} 
function clickmapmarker(marker, mapjsid, datamap){     
    marker.addListener('click', function() {
        var mrker = this;
        var mrmap = NvarMap[mrker.nikita.mapid]  ;
        //console.log(this);
                
        if (mrker.nikita.info){
            var contentString =  mrker.nikita.info;
            var infowindow = new google.maps.InfoWindow({
                content: contentString
            });
            infowindow.open( mrmap,  mrker);
        }else if (mrker.nikita.id){
            sendaction(mrker.nikita.formjsid, mrker.nikita.compjsid, mrker.nikita.actionjsid);
        }
        
        
    });
}
function addmapmarker(mapjsid, datamap){
    var vs = datamap;
     
    

    if (!Array.isArray(datamap)){
        vs = [];
        vs.push(datamap);
    }    
        
    if(vs.length>=1){
        if (vs[0].snaptoroad){
            if ( vs[0].snaptoroad === true || vs[0].snaptoroad === 'true'){
                var s = '-6.2916328,106.7833645|-6.2916328,106.7833645|-6.2916328,106.7833645|-6.2916328,106.7833645|-6.2916328,106.7833645|-6.2938575,106.7838586|-6.291313,106.7833645|-6.291313,106.7833645|-6.2938018,106.7838639|-6.2933661,106.7841313|-6.2933718,106.7840832|-6.2922602,106.7837331|-6.3010032,106.7804151|-6.2922602,106.7837331|-6.2916328,106.7833645|-6.293428,106.7842299|-6.2934588,106.7842452|-6.2934694,106.7843574|-6.2934989,106.7843585|-6.2934989,106.7843585|-6.2916328,106.7833645|-6.2947131,106.7841729|-6.2947274,106.7841606|-6.2947179,106.784046|-6.2940436,106.7849257|-6.2916328,106.7833645|-6.2942206,106.7843602|-6.2916328,106.7833645|-6.2945543,106.7844026|-6.2945543,106.7844026|-6.2916328,106.7833645|-6.2945114,106.7844383|-6.2945076,106.7844387|-6.2916328,106.7833645|-6.2944928,106.784443|-6.2944266,106.7844863|-6.2943387,106.7845339|-6.2916328,106.7833645|-6.2942834,106.7845606|-6.2916328,106.7833645|-6.2916328,106.7833645';
                    s= s+"|"+'-6.2916328,106.7833645|-6.2936487,106.7850037|-6.2934307,106.7855806|-6.2916328,106.7833645|-6.2916328,106.7833645|-6.2916328,106.7833645|-6.2916328,106.7833645|-6.2916328,106.7833645|-6.2913009,106.7837331|-6.2930829,106.784552|-6.2913009,106.7837331|-6.2913009,106.7837331|-6.2913009,106.7837331|-6.2913009,106.7837331|-6.2925003,106.7834279|-6.2923653,106.7835176|-6.2916146,106.7836379|-6.2920521,106.7834485|-6.2917856,106.7832115|-6.2911278,106.783351|-6.2906499,106.7791384|-6.2903481,106.7808892|-6.2919147,106.7871257|-6.2917826,106.7924053|-6.2908887,106.7954288|-6.2864847,106.795536|-6.2813649,106.795832|-6.278423,106.7971894|-6.2763136,106.7976311|-6.2723487,106.797521|-6.2715312,106.7975316|-6.2715312,106.7975316|-6.2715312,106.7975316|-6.2714736,106.7976048|-6.2715708,106.7974825|-6.2764599,106.7980273|-6.2736857,106.7975351|-6.2699239,106.7982061|-6.2699239,106.7982061|-6.2700485,106.7984357|-6.2697546,106.7973743|-6.2702608,106.7983579|-6.2697546,106.7973743|-6.2697546,106.7973743|-6.2697546,106.7973743|-6.2697546,106.7973743|-6.2697546,106.7973743|-6.2699301,106.7983572|-6.2699301,106.7983572|-6.2699445,106.7983796|-6.2699313,106.7983861|-6.2699071,106.7983946';
                    var apiKey = globalapikey;//'AIzaSyAmPZWSAwNOOXwlOK3jQAowoP4FKfjscmQ';
                
                /*
                var res = s.split("|");   
                 var xxx= []  ;
                for (var i = 0; i < res.length; i++){
                    var ssss={};
                   var res2 = res[i].split(","); 
                    ssss.lat=parseFloat(res2[0]) ;
                    ssss.lng=parseFloat(res2[1]) ;
                    
                    xxx.push(ssss);
                }
                console.log(JSON.stringify(xxx));
                  */
                 
               //  console.log(s);
                var lat ='';
                var lng ='';
                var out = '';
                for (var i = 0; i < vs.length ; i++){
                    
                    if (vs[i].lat){ 
                        lat = parseFloat(vs[i].lat);
                    }
                    if (vs[i].lng){ 
                        lng = parseFloat(vs[i].lng);
                    }                
                    if (vs[i].latitude){ 
                        lat = parseFloat(vs[i].latitude);
                    }
                    if (vs[i].longitude){ 
                        lng = parseFloat(vs[i].longitude);
                    }  
                    if(i===0){
                        out =  lat +','+lng;                        
                    }else{
                        out = out +'|' + lat +','+lng;
                    }
                 }
                 //console.log(out);
                 //console.log(JSON.stringify(vs));
                  
                $.post('https://roads.googleapis.com/v1/snapToRoads', {
                    interpolate: true,
                    key: apiKey,
                    path: out
                }, function(data) {
                    //  console.log(s);
                    // console.log(JSON.stringify(data));
                    
                     
                    //function processSnapToRoadResponse(data) {
                        snappedCoordinates = [];
                        placeIdArray = [];
                        for (var i = 0; i < data.snappedPoints.length; i++) {
                            var latlng = new google.maps.LatLng(
                                data.snappedPoints[i].location.latitude,
                                data.snappedPoints[i].location.longitude);
                                
                                
                                var newdata = {};
                               
                                 /*
                                if (data.snappedPoints[i].originalIndex){
                                   newdata.hidemarker = false;
                                } else{
                                   newdata.hidemarker = true;
                                }
                                newdata.path = true ; 
                                 */
                                
                                //newdata.path = true ; 
                                if (data.snappedPoints[i].originalIndex || data.snappedPoints[i].originalIndex >= 0 ){
                                    //console.log( data.snappedPoints[i].originalIndex );
                                    //console.log(JSON.stringify(vs[ data.snappedPoints[i].originalIndex ]));
                                    
                                    newdata = vs[ data.snappedPoints[i].originalIndex ];
                                } else{
                                    newdata.hidemarker = true;  
                                }
                                newdata.lat = data.snappedPoints[i].location.latitude;
                                newdata.lng = data.snappedPoints[i].location.longitude;
                                
                               
                                
                                snappedCoordinates.push(newdata);
                            //placeIdArray.push(data.snappedPoints[i].placeId);
                        }
                    //}//function
                    // console.log(mapjsid);  
                    // console.log(JSON.stringify(snappedCoordinates));  
                    drawmapmarker(mapjsid, snappedCoordinates);
                });
                return;
            }
        }
    }
    drawmapmarker(mapjsid, datamap);
}
function drawmapmarker(mapjsid, datamap){
    try {
        
        var vs = datamap;
        var lat = 0;
        var lng = 0;
        
        
        var path;
        var bpath = '';
        var pathcolor = '#000000';
        var pathweight = 3;
            
        for (var i = 0; i < vs.length; i++){
            try {
                lat = 0;
                lng = 0;
                
                if (vs[i].lat){ 
                    lat = parseFloat(vs[i].lat);
                }
                if (vs[i].lng){ 
                    lng = parseFloat(vs[i].lng);
                }                
                if (vs[i].latitude){ 
                    lat = parseFloat(vs[i].latitude);
                }
                if (vs[i].longitude){ 
                    lng = parseFloat(vs[i].longitude);
                }   
                if (vs[i].pathcolor){   
                      pathcolor=vs[i].pathcolor;
                }    
                if (vs[i].pathweight){   
                      pathweight=vs[i].pathweight;
                } 
                
                var latlng =  new google.maps.LatLng(lat, lng);
                var markerdata = {};
                var map;
                var hidemarker=false;

                if (i===0){
                    var mapOptions = {};
                    if (vs[0].zoom){
                        mapOptions.zoom = parseInt(vs[0].zoom);
                    }
                    if (vs[i].center){ 
                        mapOptions.center=latlng;
                    }
                    
                    mapOptions.mapTypeId = google.maps.MapTypeId.ROADMAP;           
                    if (NvarMap[mapjsid.concat("_map")]){
                        map = NvarMap[mapjsid.concat("_map")];   
                         
                    }else{
                        map = new google.maps.Map(document.getElementById("#".concat(mapjsid).concat("_map")),  mapOptions);   
                        NvarMap[mapjsid.concat("_map")] = map;
                    }  
                    
                    if (NvarMapPath[mapjsid.concat("_map")]){
                        path = NvarMapPath[mapjsid.concat("_map")];  
                    }else{
                        //create path
                        path = new google.maps.Polyline({
                            strokeColor: pathcolor,
                            strokeOpacity: 1.0,
                            strokeWeight: pathweight
                        }); 
                        path.setMap(map);
                       
                        NvarMapPath[mapjsid.concat("_map")] = path;
                    }
                    if (vs[i].path){
                        bpath = vs[i].path;                
                    }
                }

                if (vs[i].center){ 
                    map.panTo(latlng);
                    //map.setCenter(latlng);
                } 
               
                 if (vs[i].zoom){ 
                    map.setZoom(parseInt(vs[i].zoom));
                    //map.setCenter(latlng);
                }
                
                if (vs[i].hidemarker){ 
                    hidemarker = true;
                }


                if (NvarMapMarker[mapjsid.concat("_map")]){                      
                }else{
                    NvarMapMarker[mapjsid.concat("_map")] = [];
                }
                
                var nicomp = {};
                nicomp.id = '';
                if (vs[i].id){
                    nicomp.id = vs[i].id;
                }                 
                nicomp.compjsid = mapjsid;
                nicomp.formjsid = $( '#'+mapjsid).attr('nfid');                
                nicomp.actionjsid ='marker-'+nicomp.id;                  
                
                nicomp.mapid = mapjsid.concat("_map");
                if (vs[i].info){
                    nicomp.info = vs[i].info;
                }
                markerdata.nikita= nicomp;
                
                 //console.log(map);
                markerdata.position = latlng;
                markerdata.map = map;//marker.setMap(map);
                 if(hidemarker){
                  markerdata.map = null;//marker.setMap(map);
                }
                
                //markerdata.title= 'Nikita';
                //console.log(i);
                if (vs[i].icon){   
                     markerdata.icon=vs[i].icon;
                }  
                if (vs[i].title){   
                     markerdata.title=vs[i].title;
                } 
                var marker = new google.maps.Marker(markerdata);
                
               
                NvarMapMarker[mapjsid.concat("_map")].push(marker);
                clickmapmarker(marker, mapjsid.concat("_map"), '');

                try {                    
                    var xpath = path.getPath();  
                                     
                    if ( bpath === true || bpath === 'true'){
                        xpath.push(latlng);
                    }    
                }catch (e){}
                
            }catch (e){}
        } 
    }catch (e){};//console.log(e);
}
function setmapmarker(mapjsid, datamap){
    try {
        if (NvarMapMarker[mapjsid.concat("_map")]){              
            var vs = NvarMapMarker[mapjsid.concat("_map")];
            for (var i = 0; i < vs.length; i++){
                vs[i].setMap(null);
            }
        }
        NvarMapMarker[mapjsid.concat("_map")]=[];        
    }catch (e){ };
    try {
        if (NvarMapPath[mapjsid.concat("_map")]){              
            //var vs = NvarMapPath[mapjsid.concat("_map")];
            //vs.setPath([]);
            delete NvarMapPath[mapjsid.concat("_map")];
        }    
        
    }catch (e){ };
    addmapmarker(mapjsid, datamap);
}
 

function showdate(elm){
    $(elm).datepicker("show");
} 
function initA(){
        $.contextMenu({
            selector: '.1component', 
            callback: function(key, options) {
                var m = "clicked: " + key+$(this).attr('id');
                window.console && console.log(m) || alert(m); 
            },
            items: {
                "edit":  {name: "Edit", icon: "edit"},
                "cut":   {name: "Cut", icon: "cut"},
                "copy":  {name: "Copy", icon: "copy"},
                "paste": {name: "Paste", icon: "paste"},
                "delete":{name: "Delete", icon: "delete"},
                "sep1": "---------",
                "quit": {name: "Quit", icon: "quit"}
            }
        });

        $('.1component').on('click', function(e){
            //console.log('clicked', this);
            //alert('clicked');
        })
        
        $('#the-node').contextMenu({
            selector: 'li', 
            callback: function(key, options) {
                var m = "clicked: " + key + " on " + $(this).text();
                window.console && console.log(m) || alert(m); 
            },
            items: {
                "edit": {name: "Edit", icon: "edit"},
                "cut": {name: "Cut", icon: "cut"},
                "copy": {name: "Copy", icon: "copy"},
                "paste": {name: "Paste", icon: "paste"},
                "delete": {name: "Delete", icon: "delete"},
                "sep1": "---------",
                "quit": {name: "Quit", icon: "quit"}
            }
        });

    try {
        // $('#con-tblConn_grid').dataTable();
    }catch(e){}       
        $('.sTableGrid').each(function() {  
        
        var tg = $(this).DataTable({ searching : false, "bInfo":false, "bPaginate": false,"pagingType": "simple",
            "columnDefs": [ { "width": "20px", "targets": 0 }] 
        });
        try{   
            var col =  $.parseJSON( $(this).attr("colshide") ) ;
            tg.column( col ).visible( false );
        }catch(e){}   
        
        //(this).on( 'order.dt', function () {
            //alert( 'Table redrawn' );
        //} );
        $(this).removeClass('TableGrid');
    }) ;
    
    
    
}
function ontop(){
    var index = 0;
    $(".ui-front").each(function() { 
        if ($(this).zIndex()>index) {
            index=$(this).zIndex();
        }
    }) ;
    index=index+9999;
    $(".ui-widget-overlay").each(function() { 
        index=$(this).zIndex()-1;
    }) ;
    $(".ntop").each(function() {         
        $(this).appendTo("body");
        $(this).css( "zIndex", index );        
    }) ;
    
    //extend dialog
    if ($("#dialog-extend-fixed-container").length) {
        $("#dialog-extend-fixed-container").css( "zIndex", index );  
    }
}
function timerbroadcast(t){
    var reqrescode = {};
        reqrescode.responsecode='TIMER';
        reqrescode.requestcode='TIMER'; 
       // broadcast('nikitacontrolreceiver','{}',JSON.stringify(reqrescode));
    
    setInterval(function(){
        /*
        var reqrescode = {};
            reqrescode.responsecode='TIMER';
            reqrescode.requestcode='TIMER'; 
        var sendz = $.post('',{'href':'','action':'result','form':frmcode,'component':'','data':'{}','result':'{}','reqrescode':JSON.stringify(reqrescode),'mode':''});
        sendz.done(function(data){
            postdone(data);         
        });
        */
        var reqrescode = {};
        reqrescode.responsecode='TIMER';
        reqrescode.requestcode='TIMER'; 
        var data = {};
        data.origin='TIMER';
        broadcast('nikitatimerbroadcast',JSON.stringify(data),JSON.stringify(reqrescode));
    },t);   
}

function sendchat(event, msg){
    if (event.keyCode == 13) {       
        var user = document.getElementById('home--realname_text');        
        Chat.sendMessage(user.innerHTML+' : '+msg.value);
        msg.value='';   
    }
}

var Chat = {};
var Nikita = {};
var NikitaRz = {};
var nikitarzcount = 1 ;

var gSocket = null ;
 
//NikitaRz
function NikitaRzBase(host){
    var appid = '';
    var clientid = '';
    
    NikitaRz.isopen = false;
    NikitaRz.socket = null;    
    
    NikitaRz.connect = (function(host) {        
        var shost = host;
        if ('WebSocket' in window) {
            NikitaRz.socket = new WebSocket(host);
        } else if ('MozWebSocket' in window) {
            NikitaRz.socket =  new MozWebSocket(host);
        } else {
            //console.log('Error: NikitaRz is not supported by this browser.');
            return;
        }
        NikitaRz.socket.onopen = function () { 
            //onsole.log('Info: NikitaRz connection opened.');
            isopen = true;
        };

        NikitaRz.socket.onclose = function () { 
            isopen = false;
            //console.log('Info: NikitaRz closed.');
            setTimeout(function () { 
                nikitarzcount++;
                NikitaRz.connect(shost );//connect hire
            }, 1000 * nikitarzcount + 10000);    
        };
     
        NikitaRz.socket.onmessage = function (message) {
            //console.log(message.data);
            var str = message.data;
            if( str.indexOf ('{')!== -1){
                try {
                    var msg = JSON.parse(str);
                    if(msg.action === 'broadcast'){
                         // msg.receiver  
                         // msg.message   
                        broadcastlistener(msg.receiver, msg.message ,'broadcast'  );
                    }else if(msg.action === 'listener'){
                        // msg.event  
                        // msg.message   
                        broadcastlistener(msg.receiver, msg.message ,'listener'  );
                      }else if(msg.action === 'pbar'){
                        // msg.event  
                        // msg.message   
                        broadcastpbar(msg.receiver, msg.message ,'pbar'  );
                    }else if(msg.action === 'chat'){
                        // msg.sender
                        // msg.message     
                    }else if(msg.action === 'push'){
                        // msg.message                          
                    }
                }catch (e){}
            }else{
                
            }
        };
    });
    
    NikitaRz.initialize = function() {
        if (window.location.protocol == 'http:') {
            //Chat.connect('ws://' + window.location.host + '/examples/websocket/chat');
        } else {
            //Chat.connect('wss://' + window.location.host + '/examples/websocket/chat');
        }
        NikitaRz.connect(host);
    };
    
    
    NikitaRz.sendMessage = (function(message) {
        if (message != '') {
            NikitaRz.socket.send(  message);             
        }      
    });

    NikitaRz.addListener = (function(listener) {         
        var msg = {};
        msg.action = "addlistener";
        msg.receiver = listener;
        try {
            NikitaRz.socket.send( JSON.stringify(msg)  );  
        } catch (e) { }

              
    });
    NikitaRz.removeListener = (function(listener) {         
        var msg = {};
        msg.action = "removelistener";
        msg.receiver = listener;
        try {
            NikitaRz.socket.send( JSON.stringify(msg)  );   
        } catch (e) { }
             
    });
    var arrlistener = [];
     
    
    //console.log(host);
    NikitaRz.connect(host );//connect hire
}
function websocket(host){
    Chat.socket = null;

    Chat.connect = (function(host) {
        if ('WebSocket' in window) {
            Chat.socket = new WebSocket(host);
        } else if ('MozWebSocket' in window) {
            Chat.socket = new MozWebSocket(host);
        } else {
            Console.log('Error: WebSocket is not supported by this browser.');
            return;
        }
        Chat.socket.onopen = function () {
            Console.log('Info: WebSocket connection opened.');
            
            //document.getElementById('chat').onkeydown = function(event) {
            //    if (event.keyCode == 13) {
            ///        Chat.sendMessage(document.getElementById('chat').value);
            ///    }
            //};
        };

        Chat.socket.onclose = function () {
            //document.getElementById('chat').onkeydown = null;
            Console.log('Info: WebSocket closed.');
        };

        Chat.socket.onmessage = function (message) {
            Console.log(message.data);
        };
    });

    Chat.initialize = function() {
        if (window.location.protocol == 'http:') {
            //Chat.connect('ws://' + window.location.host + '/examples/websocket/chat');
        } else {
            //Chat.connect('wss://' + window.location.host + '/examples/websocket/chat');
        }
        Chat.connect(host);
    };

    Chat.sendMessage = (function(message) {
        if (message != '') {
            Chat.socket.send(  message);             
        }      
    });

    var Console = {};

    Console.log = (function(message) {
        var vbroadcast  =  getElementsByClassName("nikitareceiver");  
        for (var i = 0; i < vbroadcast.length; i++){        
            var recv = vbroadcast[i].getAttribute("receiver");
            if (recv.indexOf('nikitachatreceiver')>=0 || recv.indexOf('nikitacontrolreceiver')>=0){
                var compidz = vbroadcast[i].getAttribute("id"); 
                var console = document.getElementById(compidz);
                if (console==null){            
                }else{
                    if (message.indexOf('*')==0) {
                        
                    }else if (message.indexOf(':')>=-1) {
                        message=message.substring(message.indexOf(':')+1);
                        if (message.indexOf('*')>=0 && message.indexOf('*')<=3) {
                           if (recv.indexOf('nikitacontrolreceiver')>=0){
                                var reqrescode = {};
                                reqrescode.responsecode='NIKITACONTROL';
                                reqrescode.requestcode='NIKITACONTROL'; 
                                var data = {};
                                data.message=message;      
                                
                                var compidz = vbroadcast[i].getAttribute("id"); 
                                var formidz = vbroadcast[i].getAttribute("nfid"); //jsform

                                sendaction(formidz, compidz,'broadcast',JSON.stringify(data), JSON.stringify(reqrescode)); 
                                //broadcast('nikitacontrolreceiver',JSON.stringify(data),JSON.stringify(reqrescode));
                           }
                        }else if (recv.indexOf('nikitachatreceiver')>=0){
                            var p = document.createElement('div');
                            p.style.wordWrap = 'break-word';
                            p.innerHTML = message;
                            console.appendChild(p);
                            while (console.childNodes.length > 25) {
                                console.removeChild(console.firstChild);
                            }
                            console.scrollTop = console.scrollHeight;
                        }                        
                    }
                    
                }   
            }
        }    
    });

    Chat.initialize();
}
function getURL(url, args){
    var sendget = $.get( getdaturl(url), args );
        sendget.done(function(data){
            return data;
        }); 
        sendget.fail(function(){
            var err = {};
            err.error = 'timeout';
            return JSON.stringify(err);
        }); 
        sendget.always(function(){
              
        });
        
}
function broadcast(receiverz, data, reqrescode){    
    var vbroadcast  =  getElementsByClassName("nikitareceiver");        
    for (var i = 0; i < vbroadcast.length; i++){        
        var recv = vbroadcast[i].getAttribute("receiver");
        if (recv!=receiverz){
            
        }else{           
            var compidz = vbroadcast[i].getAttribute("id"); 
            var formidz = vbroadcast[i].getAttribute("nfid"); //jsform
         
            if (receiverz=='nikitatimerbroadcast'){
                var args1  ={};
                args1.showbusy= false;  
           
                sendactionnewpostA(args1, formidz, formidz, compidz, 'broadcast', data, reqrescode, '') ;
            }else{             
                sendaction(formidz, compidz,'broadcast',data, reqrescode); 
            }
        }
    }    
}
function broadcastpbar(receiverz, msg, reqrescode){  
    $(".nikitapbar").each(function() { 
    var clss = this.getAttribute("class"); 
        if(clss.indexOf(receiverz)>=0){
            this.value = msg;                            
        }                            

    }) ;
}
function broadcastlistener(receiverz, msg, reqrescode){    
    var vbroadcast  =  getElementsByClassName("nikitareceiver");        
    for (var i = 0; i < vbroadcast.length; i++){        
        var recv = vbroadcast[i].getAttribute("receiver");
        if (recv!=receiverz){            
        }else{           
            var compidz = vbroadcast[i].getAttribute("id"); 
            var formidz = vbroadcast[i].getAttribute("nfid"); //jsform
         
            var args1  ={};
            args1.showbusy= false;             
            sendactionnewpostA(args1, formidz, formidz, compidz, 'broadcast', msg, reqrescode, '') ;
        }
    }    
}
function sendenter(e,formidz,compidz,actionz){
    if(e.keyCode === 13){
        sendaction(formidz,compidz,actionz);  
    }
}
function sendlistener(e,formidz,compidz,actionz){
     
}
function isnumberkey(evt){
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;
    return true;
}
function maskKeyCode(evt, regex) {  
    evt = (evt) ? evt : window.event;   
    
    
    if(evt.charCode === 0){
        return true;
    }
    var charValue = String.fromCharCode(evt.charCode);
    var r = new RegExp(regex,"g");       
    var valid = r.test(charValue);
    
    if (!valid) {
       return false;
    }else{
       return true;
    }
   
}

function getdaturl(path){
    return path;
}
function getdocurl(path){
    return path;
}
var dataforms = {};
function getmobilegenertor(formidz, formclassz){
    var forms = getElementsByClassName("mobilenikitagenerator");
    
    
    for(f = 0; f < forms.length; f++){
        /*Loop Component per Form*/
        var oForm  =  getElementsByClassName(forms[f].getAttribute("id")) ;
        var idz = ''; 
        var fname = forms[f].getAttribute("nformname"); 
        var v = {};

        for (var i = 0; i < oForm.length; i++){
            idz =  oForm[i].id;        
            if (idz!=''){
                /*
                var vv = {};
                vv.i=oForm[i].getAttribute("id");//jsid
                vv.t=gettext(idz);   
                //nfile
                vv.v =getvisible(idz);
                vv.e=getenable(idz);
                vv.n=gettag(idz);//tag     
                */                
                //text,visible,enable,file,type,comment
                var vv = [];
                vv.push(gettext(idz));//[0]
                vv.push(getvisible(idz));
                vv.push(getenable(idz));
                vv.push(getfilecomp(idz));
                vv.push(gettypecomp(idz));//[4]
                vv.push(getcomment(idz));//commnet  
                vv.push(getmandatory(idz));// [6] 

                var nid =oForm[i].getAttribute("ncname");//ncname //nid
                v[nid]=vv;
            }
        }
        /*End per Form*/
        dataforms[fname] = v;
    }
    return JSON.stringify(dataforms);;
}
function showactioncomment(compidz) {
   var ax = getcomment(idz);
   var x = openURLpost('res');
} 
function showactioncombo(formidz,compidz,actionz) {
    if (document.getElementById(compidz.concat('_text'))) {
        document.getElementById(compidz.concat('_combo_text')).value = $('#'.concat(compidz.concat('_text')).concat(' option:selected')).text();
    }else if (document.getElementById(compidz.concat('_show_text'))){
        document.getElementById(compidz.concat('_show_combo_text')).value =$('#'.concat(compidz.concat('_show_text')).concat(' option:selected')).text();
    }   
    
}
function sendactiongrid(formidz, compidz, actionz, newaction, rowselected) {
    var datax = document.getElementById(compidz.concat('-header'));
    if (datax){
        datax.setAttribute("nrow", rowselected);
    }
    sendactionnewpost(formidz, formidz, compidz, actionz, '', '', newaction);
}
function sendactionnew(formidz, compidz, actionz, newaction) {
    sendactionnewpost(formidz, formidz, compidz, actionz, '', '', newaction);
}
function sendactioncombo(formidz, compidz, actionz) {
    showactioncombo(formidz, compidz, actionz);   
    sendaction(formidz, compidz, actionz);  
}
function sendaction(formidz, compidz, actionz, resultz, paramcodez) {
    sendactionpost(formidz, formidz, compidz, actionz, resultz, paramcodez);
}
function sendactionpost(formidz, formclassz, compidz, actionz, resultz, paramcodez) {
    sendactionnewpost(formidz, formclassz, compidz, actionz, resultz, paramcodez, '');
}
function sendactionnewpost(formidz, formclassz, compidz, actionz, resultz, paramcodez, newaction) {
    var name  ={};
    name.showbusy= true;   
   // var a = new 
    sendactionnewpostA(name, formidz, formclassz, compidz, actionz, resultz, paramcodez, newaction);
}

function sendactionnewpostA(args1, formidz, formclassz, compidz, actionz, resultz, paramcodez, newaction) {
    var args  ={};
    args.showbusy = args1.showbusy;   
    args.busyid=  formidz.concat("-busy");   
    args.waitid=  formidz.concat("-wait");   // "#".concat(formidz).concat("-wait");  
        
    var fid =   $(  "#".concat(args.busyid)   ).attr('fid');    
    var frmid =   fid;
    var frmnikita =  $(  "#".concat(fid)   );


    for(i = 0; i < 57; i++){
        if(frmnikita){
            if(  $( frmnikita  ).attr('frmode')  == 'nikitawindows'){
                args.busyid=  frmid.concat("-busy");   
                args.waitid=  frmid.concat("-wait"); 
                break;
            }
            var nativefrmid =  $(  "#".concat("form").concat(frmid)    );   
            if(nativefrmid){
                if ($(  nativefrmid  ).attr('fnt') === undefined ){
                    fid = $(  frmnikita  ).attr('caller');
                }else{
                    args.busyid=  frmid.concat("-busy");   
                    args.waitid=  frmid.concat("-wait"); 
                    break;
                }
            }else{
                fid = $(  frmnikita  ).attr('caller');
            }
        }   
        frmid =   fid;
        frmnikita =  $(  "#".concat(fid)   );
    }
    var prbarid = new Date().getTime();
    
    if ( $(  "#".concat(compidz)   ).attr('showbusy') =='true' ){
        args.showbusy = true;
    }else if ( $(  "#".concat(compidz)   ).attr('showbusy')=='false' ){
        args.showbusy = false;
    }
    
    if ( $(  "#".concat(compidz)   ).attr('showprogress')=='true' && args.showbusy ){
        try {
            var pb =   $(  "#".concat(args.busyid).concat("-pb")   );    
            pb.show();
            pb.progressbar( "value", 0);    
        }catch (e){}
        var probar = function () {
            if (probar){
                var vpb = $.post(getdaturl('res/progress/'),{'prbarid': prbarid });
                vpb.done(function(data){  
                    try{
                        var vdata = $.parseJSON( data );
                        var pb =   $(  "#".concat(args.busyid).concat("-pb")   );
                        pb.progressbar( "value", vdata.value );
                    }catch (e){}
                    if (probar){
                        setTimeout(probar, 9000);    
                    }
                });
                vpb.always(function(){
                    //console.log('alwaysalways');

                });
            }
        }    
        setTimeout(probar, 9000);    //setInterval(probar, 1000); 
    }else{
        try {
            var pb =   $(  "#".concat(args.busyid).concat("-pb")   );    
            pb.hide();
             
        }catch (e){}
    }
    
    
    
    
    //13k
    var x = $(  "#".concat(args.busyid)  );
        //x.css( "background-color","white" );
        //x.css( "opacity", "0.8" );
        if (args.showbusy)    {
            x.show();
            x.css("cursor","wait");  
        }
     
     /*
    var elm = document.getElementById("form".concat(formidz) );
    if (elm==null){
        var element = document.createElement('div');
        element.id = "form".concat(formidz).concat("busy")  ;
        element.innerHTML = '<div id="webform-web-busy" class="nikita-form-busy" style="overflow: hidden; position: absolute; left: 0px; width: 100%; height: 100%; bottom: 0px; right: 0px; top: 0px; opacity: 0.75; display: block; background-color: rgb(255, 255, 255);"><img style="height:32px;width:32px;position:absolute;left:50%;top:50%;" src="static/lib/css/images/busy.gif" alt="Loading.."></div>';
        elm.appendChild(element);
    }
    */
        

    var oForm  =  getElementsByClassName(formclassz);
    var idz = ''; 
    var v = {};
     
    for (var i = 0; i < oForm.length; i++){
        idz =  oForm[i].id;        
        if (idz!=''){
            var vv = {};
            vv.i=oForm[i].getAttribute("id");//jsid
            vv.t=gettext(idz);   
            //nfile
            vv.v =getvisible(idz);
            vv.e=getenable(idz);
            vv.n=gettag(idz);//tag     
            vv.c=getcomment(idz); 
            vv.m=getmandatory(idz);  
            
            var nid =oForm[i].getAttribute("nid");
            v[nid]=vv;
        }
    }
    
    var nid =$("#".concat(compidz)).attr("nid") ;
    if(nid){
        //component
        var vv = {};
        vv.i= $("#".concat(compidz)).attr("id") ;//jsid
        vv.t=gettext(compidz);   
        
        //nfile
        vv.v =getvisible(compidz);
        vv.e=getenable(compidz);
        vv.n=gettag(compidz);  
        vv.c=getcomment(compidz); 
        vv.m=getmandatory(compidz);  
        
        v[nid]=vv;
    }
    
    //form
    var vv = {};
    vv.i=$("#".concat(formidz)).attr("id");//equ formidz
    vv.fmode=$("#".concat(formidz)).attr("frmode");//windows or form
    vv.fcall=$("#".concat(formidz)).attr("caller");//formid caller
    vv.fcode=$("#".concat(formidz)).attr("reqcod");//requescode caller
    vv.fnikita=$("#".concat(formidz)).attr("fnikita");//parameter 
    
    vv.param=$("#".concat(formidz)).attr("reqcod");//parameter
    vv.instance=$("#".concat(formidz)).attr("instance");//parameter
    findex = vv.instance;
    
    //screen.width
    var info = {};    
    info.width = window.innerWidth
        || document.documentElement.clientWidth
        || document.body.clientWidth;

    info.height = window.innerHeight
        || document.documentElement.clientHeight
        || document.body.clientHeight; 
    info.localdate = localdate();  
    info.version = 'nv2.1.2';
    info.theme = $("#nikitageneratorversionnv3").attr("theme");
    info.nikita = $("#nikitageneratorversionnv3").html();
     
    var mobilegenerator = getmobilegenertor(formidz, formclassz); 
    //console.log(mobilegenerator); 
    
    var ninfo= JSON.stringify(info);

    var nform = $("#".concat(compidz)).attr("nform");//formidz
    nform=nform?nform: $("#".concat(formidz)).attr("nform") ;
     
    var ncomp = $("#".concat(compidz)).attr("nid");
    v["form-".concat(nform)]=vv;
   
    var json = JSON.stringify(v);
    var href = window.location.href;    
  
   
    //var sendz = $.post('',{'href':href,'nikitaformv3':JSON.stringify(vv),'action':actionz,'nikitanewaction':newaction,'form':nform,'component':ncomp,'data':json,'result':resultz,'reqrescode':paramcodez,'mode':'','ninfo':ninfo,'prbarid': prbarid});
    //Base64.encode (JSON.stringify( datanson ))
    var datanson = {'href':href,'nikitaformv3':JSON.stringify(vv),'action':actionz,'nikitanewaction':newaction,'form':nform,'findex':findex,'component':ncomp,'data':json,'result':resultz,'reqrescode':paramcodez,'mode':'','ninfo':ninfo,'prbarid': prbarid, 'mobilegenerator':mobilegenerator} ;
    var contenttype = 'application/nikitagenerator; nfid=';
    if(requestcontentdata==='base64'){
        datanson = Base64.encode (JSON.stringify( datanson ));
        contenttype = 'application/nikitagenerator; nfid=base64';
    }else if(requestcontentdata===''){
        contenttype = 'application/x-www-form-urlencoded';
    }else if(requestcontentdata==='generator'){
        datanson = Base64.encode (JSON.stringify( datanson ));
        datanson = {'nikitageneratorarguments':datanson};        
        contenttype = 'application/x-www-form-urlencoded';
    }else{                 
        datanson = (JSON.stringify( datanson ));
        contenttype = 'application/nikitagenerator; nfid=';
    }
    
    var vdata = "";
    if (actionz == 'nclient'){        
        try {
            vdata = nclient.sendaction(datanson);
            
        }catch (e){}  
        x = $( "#".concat(args.busyid)  ).hide();  
            postdone(vdata);
            if(probar){
                clearInterval(probar); 
            } 
            probar = null;
            return;
    }
    
    try {
         var sendz =   $.ajax({
                    type: "POST",
                    url: getdaturl(''),
                    data:   datanson,
                    success: function(){  },
                    dataType: 'text',
                    contentType : contenttype
                  });
    //sendz.overrideMimeType("text/plain; charset=x-user-defined");  

        sendz.done(function(data){  
            x = $( "#".concat(args.busyid)  ).hide();            
            if (args.showbusy) {
                var x = $( "#".concat(args.waitid)  );
                x.css( "background-color","black" );
                x.css( "opacity", "0.75" );
                x.show();
                x.css("cursor","wait");  
            }
           
            try {
                postdone(data); 
            } catch (e) { }
            

            if (args.showbusy)    {
                $(".nikita-form-wait").each(function() {          
                    var x = $(this);
                    if ( x.attr('id')  == args.waitid ){
                       x.hide();
                       x.css("cursor","default");  
                    }                       
                }) ;                
            }
            //$( args.waitid ).hide();
        });
        sendz.always(function(){
            if(probar){
               clearInterval(probar); 
            } 
            probar = null;
        });
        sendz.fail(function(jqXHR, textStatus){
            $( "#".concat(args.busyid)  ).hide();           
            if(textStatus === 'error' && ajaxerror.length >=1)   {     
                alert(ajaxerror);                     
            }
        });
    } catch (e) {}

    
   
  
}
var ajaxerror = '';
function openURLpost(url, data){
    return $.ajax({
        type: "POST",
        url: getdaturl(url),
        data: data,
        cache: false,
        async: false
    }).responseText;
}
function openURL(url, data){
    return $.ajax({
        type: "GET",
        url: getdaturl(url),
        data: data,
        cache: false,
        async: false
    }).responseText;
}
function aopenURLpost(url, data){
    return $.ajax({
        type: "POST",
        url: getdaturl(url),
        data: data,
        cache: false,
        async: true
    }).responseText;
}
function aopenURL(url, data){
    return $.ajax({
        type: "GET",
        url: getdaturl(url),
        data: data,
        cache: false,
        async: true
    }).responseText;
}
function lastsaved(){     
    try {
        //document.body =localStorage.getItem("nikita");
    } catch (e){}   
}

function localdate(){
    var v = {};
    var n = new Date().getTimezoneOffset();
    
    v.date = getDateTime();
    v.time = Date.now();    
    v.timezone = n*(-1)/60;//+hour
    v.utc = n*(-1);//-minutes == > minute

    return v;
}
function getDateTime() {
    var now     = new Date(); 
    var year    = now.getFullYear();
    var month   = now.getMonth()+1; 
    var day     = now.getDate();
    var hour    = now.getHours();
    var minute  = now.getMinutes();
    var second  = now.getSeconds(); 
    if(month.toString().length == 1) {
        var month = '0'+month;
    }
    if(day.toString().length == 1) {
        var day = '0'+day;
    }   
    if(hour.toString().length == 1) {
        var hour = '0'+hour;
    }
    if(minute.toString().length == 1) {
        var minute = '0'+minute;
    }
    if(second.toString().length == 1) {
        var second = '0'+second;
    }   
    var dateTime = year+'-'+month+'-'+day+' '+hour+':'+minute+':'+second;   
    return dateTime;
}
function postdone(data){
    if (startsWith(data,'<')){
        document.open();
        document.write(data);
        document.close();
    }else{
        try{
            //localStorage.setItem("nikita", data)
            var svrdata = $.parseJSON( data );
            for (var i = 0; i < svrdata.length; i++){
                var sdata =  svrdata[i];
                if(sdata.code.indexOf('showform')>-1){
                    var elm = document.getElementById(sdata.id);
                    if (elm==null){
                        var element = document.createElement('div');
                        element.id = sdata.id;
                        element.innerHTML = sdata.data;
                        document.body.appendChild(element);
                    }else{
                        elm.innerHTML = sdata.data;  
                    }
                    showform(sdata.id, sdata.text, sdata.modal, $.parseJSON(sdata.style), sdata.consume, sdata.fid , sdata.fname );
                    try {
                         init();
                    }catch(e){}  
                    var rdata = {};
                    rdata['name'] = sdata.fname;
                    rdata['reqcode'] = sdata.reqcode;                    
                    broadcast('nikitawebshow',JSON.stringify(rdata), sdata.fname);
                    //localStorage.setItem("nikita", document.body.innerHTML);
                }
                if(sdata.code === 'local'){
                    $("#nikitageneratorversionnv3").html(sdata.data);
                }
                if(sdata.code === 'addlistener'){
                      console.log(NikitaRz);
                    if (NikitaRz !=='undefined'){
                          console.log('NikitaRz');
                        try {
                            NikitaRz.addListener(sdata.receiver);
                        }catch (e){}
                    }
                }
                if(sdata.code === 'removelistener'){
                    console.log(NikitaRz);
                    if (NikitaRz !=='undefined'){
                        console.log('NikitaRz');
                        try {
                            NikitaRz.removeListener(sdata.receiver);
                        }catch (e){}                        
                    }
                }
                if(sdata.code.indexOf('localdate')>-1){
                    var v = localdate();
                      
                    v = JSON.stringify(v) ;
                    sendaction(sdata.id, sdata.id,'result', v, sdata.reqrescode);        
                }
                if(sdata.code.indexOf('broadcast')>-1){
                    broadcast(sdata.receiver, sdata.data, sdata.reqrescode);
                }
                 
                if(sdata.code.indexOf('contentform')>-1){
                    var elm = document.getElementById(sdata.id);//target
                    if (elm==null){
                    }else{                    
                        elm.innerHTML = sdata.data; 
                        elm.setAttribute('xid',sdata.id);
                        elm.setAttribute('xfid',sdata.fid);
                        try {
                            init();
                        } catch (e){} 
                    }
                        
                        /*
                    var ux = false;                    
                    if (elm==null){ 
                        console.log(0);
                    }else{
                        console.log("1.hideoldcontent");
                        if (sdata.hideoldcontent){
                            // this.getAttribute("class"); 
                            var contentid = elm.getAttribute("xfid");
                             console.log(11);
                             console.log(contentid);
                            if (contentid){
                                 console.log(12);
                                //pindahkan dulu
                                if (contentid === sdata.fid){
                                     console.log(13);
                                      console.log("sama yg dibuka nggk berlu dipindah");
                                    //sama yg dibuka nggk berlu dipindah
                                }else{
                                     console.log(14);
                                     console.log("pindah sekarang");
                                    //pindah sekarang
                                    
                                   
                                   // var element = document.createElement('div');
                                    //element.id = sdata.fid + "_contentform";
                                    //element.innerHTML = contentid.innerHTML;
                                   // element.style.visibility = "hidden";
                                   // document.body.appendChild(element);
                                   
                                   console.log(elm.childNodes[0]);
                                    var element = document.createElement('div');
                                    element.id = sdata.fid + "_contentform";
                                    element.appendChild(elm.childNodes[0]);
                                    element.style.visibility = "hidden";
                                    document.body.appendChild(element);
                                    
                                    
                                    console.log(15);
                                }                                
                            }else{
                                 console.log(19);
                            }                            
                        }
                         console.log("2.useexisting");
                        if (sdata.useexisting){                            
                            var contentid = elm.getAttribute("xfid");
                             console.log(21);
                             console.log(contentid);
                            if (contentid){
                                 //ada content sama     
                                if (contentid === sdata.fid){
                                     console.log(22);
                                    //sama yg dibuka nggk berlu dipindah
                                }else{
                                     console.log(23);
                                    ux = true;
                                }
                            } else{
                                 console.log(24);
                                ux = true;                                  
                            }                                                          
                        }else{
                              console.log("old");
                          
                            elm.innerHTML = sdata.data; 
                            elm.setAttribute('xid',sdata.id);
                            elm.setAttribute('xfid',sdata.fid);
                            try {
                                init();
                            } catch (e){} 
                        }
                         console.log("3:Uc");
                        if (ux){
                              console.log("sama yg dibuka nggk berlu dipindah");
                                var contentid = document.getElementById(sdata.fid+ "_contentform");
                                 console.log(31);
                                 console.log(contentid);
                                 
                                //found content
                                if (contentid){
                                    console.log(32);
                                   
                                    console.log(contentid.childNodes[0]);
                                    elm.appendChild(contentid.childNodes[0]);
                                    //elm.innerHTML = contentid.innerHTML; 
                                    elm.setAttribute('xid',sdata.id);
                                    elm.setAttribute('xfid',sdata.fid);
                                    console.log(33);
                                    document.body.removeChild(contentid);
                                   
                                    console.log(34);
                                } else{
                                    console.log(35);
                                   
                                    elm.innerHTML = sdata.data; 
                                    elm.setAttribute('xid',sdata.id);
                                    elm.setAttribute('xfid',sdata.fid);
                                    console.log(36);
                                    try {
                                        init();
                                    } catch (e){}  
                                    
                                }  
                        }else{
                          
                            
                        }                        
                    }
                    */
                    
                    $(".nikitaformtitle").each(function() { 
                        var clss = this.getAttribute("class"); 
                            if(clss.indexOf(sdata.reqcode)>=0){
                                if(sdata.text){
                                    this.innerHTML = sdata.text; 
                                }                               
                            }                            
                                                
                    }) ;
                }
                if(sdata.code === 'badge'){  
                    $(".nikitabadge").each(function() { 
                    var clss = this.getAttribute("class"); 
                        if(clss.indexOf(sdata.class)>=0){
                            if(sdata.badge){
                                this.style.removeProperty('display'); 
                                this.innerHTML = sdata.badge; 
                            }else if(sdata.badg == ''){
                                this.style.display = 'none';
                            } else{
                                this.style.display = 'none';
                            }                              
                        }                            

                    }) ;
                }
                
                if(sdata.code === 'pbar'){  
                    $(".nikitapbar").each(function() { 
                    var clss = this.getAttribute("class"); 
                        if(clss.indexOf(sdata.class)>=0){
                            if(sdata.value){                              
                                this.value = sdata.value;  
                            }                              
                        }                            

                    }) ;
                }
                
                if(sdata.code === 'other'){  
                    $(".nikitaother").each(function() { 
                    var clss = this.getAttribute("class"); 
                        if(clss.indexOf(sdata.class)>=0){
                            if(sdata.text){
                               this.innerHTML = sdata.text;                          
                            } else{
                                this.innerHTML = '';
                            }                              
                        }                            

                    }) ;
                }
                
                if(sdata.code.indexOf('addmapmarker')>-1){
                    addmapmarker(sdata.id, sdata.data);
                }        
                if(sdata.code === 'setmapmarker'){
                    setmapmarker(sdata.id, sdata.data);
                }   
                
                
                if(sdata.code === 'daturl'){
                     daturl = sdata.daturl;
                }   
                if(sdata.code === 'docurl'){
                     docurl = sdata.docurl;
                }
                
                if(sdata.code.indexOf('showbusy')>-1){
                    showbusy();
                }
                if(sdata.code.indexOf('closebusy')>-1){
                    closebusy();
                }
                if(sdata.code.indexOf('callwait')>-1){
                    try {
                        varwait=1;  ///??              
                    } catch (e){}     
                }
                if(sdata.code.indexOf('checkfile')>-1){
                    try {       
                       
                        
                        $(  "#".concat(sdata.id) ).attr("filename", sdata.text);
                        $(  "#".concat(sdata.id).concat('_fn')  ).attr("value", sdata.text);
                        $(  "#".concat(sdata.id) ).attr("fname", sdata.fid)
                        var filename = "";
                         
                        var filesize = 0;
                        var fileInput =  document.getElementById( sdata.fileid );
                        try{
                            filesize = fileInput.files[0].size; // Size returned in bytes.
                            filename = fileInput.files[0].name; // Size returned in bytes.
                        }catch(e){
                            try{
                                var objFSO = new ActiveXObject("Scripting.FileSystemObject");
                                var e = objFSO.getFile( fileInput.value);
                                filesize =e.size; 
                                filename =e.name; 
                            }catch(e){ }
                        }                          
                        
                                              
                        var reqcode = 'checkfile';
                        var frmcode = sdata.fid ; // $( "#".concat(sdata.id)  ).attr('fname');
                        var rescode = sdata.text; 
                        var resultz = {}; 
                         
                        resultz.filesize=filesize;     
                        resultz.filename=filename;    

                        var reqrescode = {};
                        reqrescode.responsecode=rescode;
                        reqrescode.requestcode=reqcode;
                        sendactionpost(frmcode, frmcode,'nikita-upload-listener','result',JSON.stringify(resultz),JSON.stringify(reqrescode));

                    } catch (e){}     
                }
                if(sdata.code.indexOf('submit')>-1){
                    //myWindow = window.open("", "nikitaupload", "width=100, height=20");
                  
                    try {
                        
                        $(  "#".concat(sdata.id) ).attr("filename", sdata.text);
                        $(  "#".concat(sdata.id).concat('_fn')  ).attr("value", sdata.text);
                        $(  "#".concat(sdata.id) ).attr("fname", sdata.fid)
                        var finame
                         
                        var filesize = 0;
                        var fileInput =  document.getElementById( sdata.fileid );
                        try{
                            filesize = fileInput.files[0].size; // Size returned in bytes.
                        }catch(e){
                            try{
                                var objFSO = new ActiveXObject("Scripting.FileSystemObject");
                                var e = objFSO.getFile( fileInput.value);
                                filesize =e.size; 
                            }catch(e){ }
                        }   

                        var filemax = 0;
                        try{
                             filemax = parseInt(  $("#".concat(sdata.id) ).attr('maxsize')  ) ;
                        }catch(e){ }
                        
                            
                            
                        if ((filemax>=13 && filesize>13 && filesize>filemax) || fileInput.value == ""){
                            
                            var reqcode = 'submit';
                           
                            var frmcode = sdata.fid ; // $( "#".concat(sdata.id)  ).attr('fname');
                     
                            var rescode = sdata.text; 
                           
                            var resultz = {}; 
                       
                            if (fileInput.value == ""){
                                resultz.error='Value Empty';
                            }else{
                                resultz.error='ContentLength File size Limit';   
                            }
                            resultz.filesize=filesize;
                            resultz.filemax=filemax;
                           
                            var reqrescode = {};
                            reqrescode.responsecode=rescode;
                            reqrescode.requestcode=reqcode;
                            sendactionpost(frmcode, frmcode,'nikita-upload-listener','result',JSON.stringify(resultz),JSON.stringify(reqrescode));
                            //sendaction(sdata.id, sdata.id,'result', sdata.data, sdata.reqrescode);
                        }else{
                          
                            $("#".concat(sdata.id)).submit();
                            //sendactionpost(frmcode, frmcode,'nikita-upload-listener','result',JSON.stringify(resultz),JSON.stringify(reqrescode));
                        }
                    } catch (e){ console.log('catch');console.log(e);}     
                    //myWindow.close();             
                }
                 
                if(sdata.code.indexOf('body')>-1){
                    var elm = document.body;
                    elm.innerHTML = sdata.data; 
                    try {
                        init();
                    } catch (e){}    
                    
                }
                if(sdata.code.indexOf('content')>-1){
                     
                    var elm = document.getElementById(sdata.id);                    
                    try {
                        elm.innerHTML = sdata.data; 
                        init();                        
                    } catch (e){}   
                    
                    /*
                    try {
                        var xName = $( "#".concat(formidz) ).getAttribute("instance");
                        if ( $( "#form".concat(xName) )  ){
                            $( "#form".concat(xName) ).parent().append( $(  "#".concat(formidz).concat("-busy")  ) );
                            $( "#form".concat(xName) ).parent().append( $(  "#".concat(formidz).concat("-wait")  ) );
                        }
                        $( "#".concat(formidz) ).parent().append( $(  "#".concat(fid).concat("-busy")  ) );
                        $( "#".concat(formidz) ).parent().append( $(  "#".concat(fid).concat("-wait")  ) );             
                    } catch (e){} 
                    */
                    
                }
                if(sdata.code == 'addclass'){
                    $( '#'.concat(sdata.id) ).addClass( sdata.class );
                }
                if(sdata.code == 'removeclass'){
                    $( '#'.concat(sdata.id) ).removeClass( sdata.class );
                }
                if(sdata.code == 'setattr'){
                    var elm = document.getElementById(sdata.id);
                    
                    if (sdata.style!=null){
                        var vs = $.parseJSON(sdata.style);
                        for (var key in vs) {
                            if (elm.style.setProperty) {
                                elm.style.setProperty (key, vs[key], null);
                            }
                        }
                    }
                }                
                
                if(sdata.code.indexOf('style')>-1){
                    var elm = document.getElementById(sdata.id);
                    
                    if (sdata.style!=null){
                        var vs = $.parseJSON(sdata.style);
                        for (var key in vs) {
                            if (elm.style.setProperty) {
                                elm.style.setProperty (key, vs[key], null);
                            }
                        }
                    }
                }
                if(sdata.code.indexOf('refresh')>-1){
                    var elm = document.getElementById(sdata.id);                    
                    
                    var smgrid = false;var smgridwh = [];
                    if(sdata.type === 'smartgrid'){
                        var $tds =  $('#'+sdata.id+'-header').find("th");
                        $.each($tds, function() {
                            smgrid = true;
                            smgridwh.push($(this).css("width"));
                        });
                    }
                    
                    
                    
                    elm.id=sdata.id.concat('refreshnikitawilly');
                    elm.innerHTML = sdata.data; 
                    
                    var chil = document.getElementById(sdata.id);
                    elm.innerHTML = chil.innerHTML ;
                    elm.id=sdata.id; //setAttribute("id", "yyy");
                    
                    
                    
                    if (sdata.style!=null){
                        var vs = $.parseJSON(sdata.style);
                        for (var key in vs) {
                            if (elm.style.setProperty) {
                                elm.style.setProperty (key, vs[key], null);
                            }
                        }
                    }
                    try {
                        init();
                    } catch (e){}   
                    
                    if(smgrid === true){
                        var smgridcnt = 0;
                        var $tds =  $('#'+sdata.id+'-header').find("th");
                        $.each($tds, function() {
                            $(this).css("width", smgridwh[smgridcnt] );                             
                            smgridcnt++;
                        });                    
                    }                    
                }
                if(sdata.code.indexOf('result')>-1){
                    sendaction(sdata.id, sdata.id,'result',sdata.data, sdata.reqrescode);
                    //init();
                }
              if(sdata.code == 'viewvisibility'){
                    viewvisibility(sdata.id, sdata.vvisibility);
                }
                if(sdata.code == 'viewmandatory'){
                    viewmandatory(sdata.id, sdata.vmandatory);
                }
                if(sdata.code.indexOf('viewmandatoryerror')>-1){
                    viewmandatoryerror(sdata.id, sdata.verror);
                }
                if(sdata.code.indexOf('closeform')>-1){
                    closeform(sdata.id, sdata.fname);
                }
                if(sdata.code.indexOf('backform')>-1){
                    backform(sdata.id, sdata.fname);
                }         
                if(sdata.code.indexOf('cleardataform')>-1){
                    cleardataform(sdata.id, sdata.fname);
                }  
                
                
                if(sdata.code.indexOf('dialog')>-1){                    
                    showdialog(sdata.formid, sdata.title, sdata.message, sdata.reqcode, $.parseJSON( sdata.button ) )
                }
                if(sdata.code.indexOf('setlocalstorage')>-1){
                    try {
                        localStorage.setItem(sdata.key, sdata.data);
                    } catch (e){}   
                }
                if(sdata.code.indexOf('getlocalstorage')>-1){
                    try {
                        sdata.data=localStorage.getItem(sdata.key);
                        sendaction(sdata.id, sdata.id,'result', sdata.data, sdata.reqrescode);
                    } catch (e){}                       
                }
                if(sdata.code.indexOf('open')>-1){
                    //equvalent with redirect
                   // var bfr = window.onbeforeunload ;
                    //window.onbeforeunload = function() { };
                    if(sdata.id.length>=2){  
                        if(sdata.id.indexOf('close')>-1){
                            var myWindow = window.open(sdata.data,'close');
                            myWindow.close();
                        }else{
                            window.open(sdata.data, sdata.id);
                        }                         
                    }else{
                        window.open(sdata.data,'_self');
                    }  
                    //window.onbeforeunload=bfr;
                } 
                if(sdata.code.indexOf('diapopup')>-1){                      
                    var heightdialog = window.outerHeight-120;
                    var widthdialog = window.outerWidth-120;

                    var leftdialog  = (screen.width-widthdialog)/2;
                    var topdialog   = (screen.height-heightdialog)/2;
                    
                    var popup = window.open (sdata.data, 'dialogpopup', 'width='+widthdialog+', height='+heightdialog+', top='+topdialog+', left='+leftdialog);
                } 
                if(sdata.code.indexOf('client')>-1){
                    var args = $.parseJSON( sdata.args );                  
                    if(sdata.methode.indexOf('post')>-1){
                         var napp =  aopenURLpost( sdata.data ,args);
                    }else{
                        var napp =  aopenURL( sdata.data ,args);
                    }                           
                    //sendaction(sdata.id, sdata.id,'result', napp, sdata.reqrescode);                    
                } 
                if(sdata.code.indexOf('reload')>-1){
                    window.onbeforeunload = function() { };
                    location.reload(true);
                   
                }   
                if(sdata.code.indexOf('sendchat')>-1){
                    try {
                        Chat.sendMessage(sdata.data);  
                    }catch (e){}                                     
                }   
               
                if(sdata.code.indexOf('alert')>-1){
                    alert(sdata.data);
                    
                }
                if(sdata.code.indexOf('getlocalcomport')>-1){
                    if (NikitaApp) {
                        sdata.data = NikitaApp.getComport(sdata.com, sdata.boudrate, sdata.databit, sdata.stop, sdata.parity);
                        sendaction(sdata.id, sdata.id,'result', sdata.data, sdata.reqrescode);
                    }
                }
                if(sdata.code.indexOf('getlocalcamera')>-1){
                    if (NikitaApp) {
                        sdata.data = NikitaApp.getCameraCapture(sdata.com);
                        sendaction(sdata.id, sdata.id,'result', sdata.data, sdata.reqrescode);
                    }
                }
                if(sdata.code.indexOf('getlocalfile')>-1){
                    if (NikitaApp) {
                        sdata.data = NikitaApp.getFile(sdata.file);
                        sendaction(sdata.id, sdata.id,'result', sdata.data, sdata.reqrescode);
                    }
                }
                if(sdata.code.indexOf('clientprint')>-1){
                    if (typeof NikitaApp  === 'undefined' ) {
                        var v = {};                        
                        v.error = 'NikitaApp is undefined';
                        v = JSON.stringify(v) ;
                        sendaction(sdata.id, sdata.id,'result', v, sdata.reqrescode);
                    }else{
                                         
                        var v = NikitaApp.printData({printer:sdata.printer,file:sdata.file,data:sdata.data});                         
                        sendaction(sdata.id, sdata.id,'result', v, sdata.reqrescode);
                    }
                }
                if(sdata.code.indexOf('nikitabridge')>-1){
                    var v = getURL({url:sdata.url, args:sdata.args});
                    sendaction(sdata.id, sdata.id,'result', v, sdata.reqrescode);
                }
                if(sdata.code.indexOf('serialport')>-1){
                    if (typeof NikitaApp  === 'undefined' ) {                        
                        var v = {};                        
                        var napp =  openURL(  "http://localhost:8080/nikitabrigde/serialport/",{com:sdata.com,setting:sdata.setting,prefix:sdata.prefix,mode:sdata.mode,data:sdata.data});
                        sendaction(sdata.id, sdata.id,'result', napp, sdata.reqrescode);
                    }else{
                        var s = NikitaApp.serialPort({com:sdata.com,setting:sdata.setting,prefix:sdata.prefix,mode:sdata.mode,data:sdata.data});
                        sendaction(sdata.id, sdata.id,'result', s, sdata.reqrescode);
                    }
                }
                if(sdata.code.indexOf('printpdf')>-1){
                    if (typeof NikitaApp  === 'undefined' ) {
                        
                        var v = {};                        
                        v.error = 'NikitaApp is undefined';
                        v = JSON.stringify(v) ;
                        var napp =  openURL(  "http://localhost:8080/nikitabrigde/printpdf/",{url:sdata.url,printer:sdata.printer,mode:sdata.mode,data:sdata.data});
                        
                        sendaction(sdata.id, sdata.id,'result', napp, sdata.reqrescode);
                    }else{
                        var s = NikitaApp.printPdf({url:sdata.url,printer:sdata.printer,mode:sdata.mode,data:sdata.data});
                        //v = JSON.stringify(v) ;
                        sendaction(sdata.id, sdata.id,'result', s, sdata.reqrescode);
                    }
                }
                if(sdata.code.indexOf('datetime')>-1){                     
                    var v = {};
                    v.datetime = getDateTime();
                    v = JSON.stringify(v) ;
                    sendaction(sdata.id, sdata.id,'result', v, sdata.reqrescode);                
                }
                if(sdata.code.indexOf('location')>-1){                     
                    var v = {};
                    if (typeof NikitaApp  === 'undefined' ) {
                        if (navigator.geolocation) {
                            navigator.geolocation.getCurrentPosition(function(position){
                                var coords = {};
                                coords.accuracy = position.coords.accuracy;
                                coords.altitude  = position.coords.altitude;
                                coords.altitudeAccuracy  = position.coords.altitudeAccuracy;
                                coords.heading  = position.coords.heading;
                                coords.latitude  = position.coords.latitude;
                                coords.longitude  = position.coords.longitude;
                                coords.speed  = position.coords.speed;
                                coords.timestamp  = position.timestamp;

                                var locpos = {};
                                locpos.coords  = coords;
                                
                                //nikita standart gps
                                locpos.time  = position.timestamp;
                                locpos.accuracy = position.coords.latitude;
                                locpos.altitude  = position.coords.altitude;
                                locpos.provider  = 'browser';
                                locpos.latitude  = position.coords.latitude;
                                locpos.longitude  = position.coords.longitude;
                                locpos.speed  = position.coords.speed;
                                
                                
                                v = JSON.stringify(locpos) ;
                                sendaction(sdata.id, sdata.id,'result', v, sdata.reqrescode);
                            }, function(error){
                                v.error = error.code;
                                
                                switch(error.code) {
                                case error.PERMISSION_DENIED:
                                    v.code = 'PERMISSION_DENIED';
                                    v.message = "User denied the request for Geolocation."
                                    break;
                                case error.POSITION_UNAVAILABLE:
                                    v.code = 'POSITION_UNAVAILABLE';
                                    v.message = "Location information is unavailable."
                                    break;
                                case error.TIMEOUT:
                                    v.code = 'TIMEOUT';
                                    v.message = "The request to get user location timed out."
                                    break;
                                case error.UNKNOWN_ERROR:
                                    v.code = 'UNKNOWN_ERROR';
                                    v.message= "An unknown error occurred."
                                    break;
                                }
                                
                                v = JSON.stringify(v) ;
                                sendaction(sdata.id, sdata.id,'result', v, sdata.reqrescode);
                            });

                        }else{
                            v.error = 'null';
                            v = JSON.stringify(v) ;
                            sendaction(sdata.id, sdata.id,'result', v, sdata.reqrescode);
                        } 
                    }else{
                        sendaction(sdata.id, sdata.id,'result', v, sdata.reqrescode);
                    }                
                }
                if(sdata.code.indexOf('requestfocus')>-1){
                    try {
                        document.getElementById(sdata.id).focus();
                    } catch (e){}
                }                
            }
        }catch(e){
            //alert(e);
        }
        //alert(data);
    }
}


// Create Base64 Object
var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(e){var t="";var n,r,i,s,o,u,a;var f=0;e=Base64._utf8_encode(e);while(f<e.length){n=e.charCodeAt(f++);r=e.charCodeAt(f++);i=e.charCodeAt(f++);s=n>>2;o=(n&3)<<4|r>>4;u=(r&15)<<2|i>>6;a=i&63;if(isNaN(r)){u=a=64}else if(isNaN(i)){a=64}t=t+this._keyStr.charAt(s)+this._keyStr.charAt(o)+this._keyStr.charAt(u)+this._keyStr.charAt(a)}return t},decode:function(e){var t="";var n,r,i;var s,o,u,a;var f=0;e=e.replace(/[^A-Za-z0-9\+\/\=]/g,"");while(f<e.length){s=this._keyStr.indexOf(e.charAt(f++));o=this._keyStr.indexOf(e.charAt(f++));u=this._keyStr.indexOf(e.charAt(f++));a=this._keyStr.indexOf(e.charAt(f++));n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;t=t+String.fromCharCode(n);if(u!=64){t=t+String.fromCharCode(r)}if(a!=64){t=t+String.fromCharCode(i)}}t=Base64._utf8_decode(t);return t},_utf8_encode:function(e){e=e.replace(/\r\n/g,"\n");var t="";for(var n=0;n<e.length;n++){var r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r)}else if(r>127&&r<2048){t+=String.fromCharCode(r>>6|192);t+=String.fromCharCode(r&63|128)}else{t+=String.fromCharCode(r>>12|224);t+=String.fromCharCode(r>>6&63|128);t+=String.fromCharCode(r&63|128)}}return t},_utf8_decode:function(e){var t="";var n=0;var r=c1=c2=0;while(n<e.length){r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r);n++}else if(r>191&&r<224){c2=e.charCodeAt(n+1);t+=String.fromCharCode((r&31)<<6|c2&63);n+=2}else{c2=e.charCodeAt(n+1);c3=e.charCodeAt(n+2);t+=String.fromCharCode((r&15)<<12|(c2&63)<<6|c3&63);n+=3}}return t}}
function showPosition(position,imgid) {
    var latlon = position.coords.latitude + "," + position.coords.longitude;

    var img_url = "http://maps.googleapis.com/maps/api/staticmap?center="
    +latlon+"&zoom=17&size=640x480&sensor=false";
    document.getElementById(imgid).innerHTML = "<img src='"+img_url+"'>";
}

function execinitload(){
    $(".nikitageneratorformactivate").each(function() {  
        $(this).removeClass('nikitageneratorformactivate');
        var decodedString = Base64.decode($(this).html());//decodeURI
        postdone(decodedString);
        $(this).html("");
    }) ;
}
function startsWith(str, prefix) {
    return str.lastIndexOf(prefix, 0) === 0;
}
 
function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}
function gettext(compid) {
   try{
        var txtContent = $("#".concat(compid)).attr("ntype");
        if(txtContent.indexOf('nikitatext')>-1){                //mandatory.toLowerCase();
            var comJs = $( "#".concat(compid.concat('_text')) );
            if ( comJs.hasClass( "n-char-ucase" )){
                return document.getElementById(compid.concat('_text')).value.toUpperCase();
            }else if (comJs.hasClass( "n-char-lcase" )){
                return document.getElementById(compid.concat('_text')).value.toLowerCase();
            }
            if (  comJs.attr("style").indexOf('n-currency-format:true') >=0  ){               
                return  getnumbercurr(document.getElementById(compid.concat('_text')).value);
            }    
            return document.getElementById(compid.concat('_text')).value;
        }else if(txtContent.indexOf('nikitaautotext')>-1){   //autocomplete    
            return document.getElementById(compid.concat('_text')).value;
        }else if(txtContent.indexOf('nikitadate')>-1){          //mandatory
            return document.getElementById(compid.concat('_text')).value;
        }else if(txtContent.indexOf('nikitabutton')>-1){ 
            return document.getElementById(compid.concat('_text')).value;
        }else if(txtContent.indexOf('nikitalabel')>-1){
            return document.getElementById(compid.concat('_text')).innerHTML;
        }else if(txtContent.indexOf('nikitacheckbox')>-1){      //mandatory
            return gettextcheckbox(compid);
        }else if(txtContent.indexOf('nikitaradiobutton')>-1){   //mandatory
            return gettextradiobutton(compid);  
        }else if(txtContent.indexOf('nikitatablegrid')>-1){     //mandatory     
            return gettexttablegrid(compid);
        }else if(txtContent.indexOf('nikitasmargrid')>-1){      //mandatory (newcomp)     
            return gettextsmartgrid(compid);           
        }else if(txtContent.indexOf('nikitacombolist')>-1){     //mandatory
            return gettextcombolist(compid);
        }else if(txtContent.indexOf('nikitacell')>-1){
            return document.getElementById(compid).innerHTML;  
         }else if(txtContent.indexOf('nikitaarea')>-1){         //mandatory
            return document.getElementById(compid.concat('_text')).value; 
        }else if(txtContent.indexOf('nikitacombobox')>-1){      //mandatory
            return document.getElementById(compid.concat('_text')).value;
        }else if(txtContent.indexOf('nikitaupload')>-1){         //mandatory
            return $( "#".concat(compid).concat('_form') ).attr("filename");    
        }else if(txtContent.indexOf('nikitaimage')>-1){
            return document.getElementById(compid.concat('_img')).innerHTML; 
        }else if(txtContent.indexOf('nikitareceiver')>-1){         
            return $("#".concat(compid)).attr("receiver");
        }else if(txtContent.indexOf('nikitacontentaccess')>-1){         
            return gettextmenuaccess(compid);
        }    
    }catch (e){} 
    return '';
} 
function getmandatory(compid) {
    try{
        var comp = document.getElementById(compid.concat('_label_mand'));
        if (comp!=null){
            return $('#'+compid.concat('_label_mand')).css("display")=='none' ?"0":"1";
        }    
    }catch (e){} 
    return '';
}
function getcomment(compid) {
    try{
        var comp = document.getElementById(compid.concat('_comment'));
        if (comp!=null){
            return comp.value;
        }    
    }catch (e){} 
    return '';
}
function gettag(compid) {
    try{
        var comp = document.getElementById(compid.concat('_tag'));
        if (comp!=null){
            return comp.innerHTML;
        }    
    }catch (e){} 
    return '';
}
function gettextcombolist(compid){
    var checs = $('#'.concat(compid.concat('_select'))).val();
    var v = [];
            v.push(checs);   
    if(checs == null){
        return '[]';
    }
    return JSON.stringify(checs);
}
function gettextmenuaccess(compid) {
    
    var checs = document.getElementsByName(compid.concat('_text'));
    var v = [];
    for (var i= 0; i < checs.length; i++){
        if (checs[i].checked ){
            v.push(checs[i].value);//[value,value]|{value:checked,value:unchecked}
        }
    }
    return JSON.stringify(v);
}
function gettextsmartgrid(compid){
    var v = {};   
    
    v.t = [];
    var datax = document.getElementById(compid.concat('-header'));
    if (datax){
        try {
            var selectdata = [];
            var row = parseInt(datax.getAttribute("nrow") );
            var cols = parseInt(datax.getAttribute("ncols") );
            for (var i= 0; i < cols; i++){
                var datacurr = document.getElementById(compid.concat('-cell-'+row+'-'+i));
                if (datacurr){
                    selectdata.push(datacurr.innerHTML);
                } else{
                    selectdata.push('');
                }
            }
            v.t = selectdata;
        }catch (e){}
    }    
    
    
    
    
    v.bs = document.getElementById(compid.concat('_buffer')).innerHTML;//buffer selected
    
    var checs = document.getElementsByName(compid.concat('_text'));
    var vv = []; var vu = [];
    for (var i= 0; i < checs.length; i++){
        if (checs[i].checked ){
            vv.push(checs[i].getAttribute("nvalue"));
        }else{
            vu.push(checs[i].getAttribute("nvalue"));
        }
    }
    v.st = vv;//selected
    v.su = vu;//unselected
    var page= document.getElementById(compid.concat('_page'));
    if(page){
        v.pg = page.getAttribute("npage");
    }else{
        v.pg = '-1';//page
    }    
    var rowpage=document.getElementById(compid.concat('_show_text'));
    if(rowpage){
        v.sh = rowpage.value;//showrowperpahe
    }else {
        v.sh = '-1';//page
    }    
    
    var sort = document.getElementsByName(compid.concat('_sort'));
    var vv = {}; 
    for (var i= 0; i < sort.length; i++){
        vv[sort[i].getAttribute("nvalue")]=sort[i].getAttribute("nsort");
    }
    v.so = vv;//sort 
    return JSON.stringify(v); 
}
function gettexttablegrid(compid){    
    var checs = getElementsByClassName(compid.concat('_grid_cell'));
    var v = {};   
    for (var i= 0; i < checs.length; i++){
        idz =  checs[i].id;
        var vv = {};
        vv.i=checs[i].getAttribute("id");
        vv.t=gettext(idz);
        
        //nfile
        vv.n=gettag(idz); 

        var nid =checs[i].getAttribute("nid");
        v[nid]=vv;
    }        
    var comp = document.getElementById(compid.concat('_cell_tag'));
    if (comp!=null){
        idz =  comp.id;
        var vv = {};
        vv.i=idz;
        vv.n=comp.innerHTML; 
        vv.c=comp.innerHTML;
        var nid =comp.getAttribute("nid");
        v[nid]=vv;
    }  
    return JSON.stringify(v);    
}
function gettextcheckbox(compid){
    var checs = document.getElementsByName(compid.concat('_text'));
    var v = [];
    for (var i= 0; i < checs.length; i++){
        if (checs[i].checked ){
            v.push(checs[i].value);
        }
    }
    return JSON.stringify(v);
}

function gettextradiobutton(compid){
    var checs = document.getElementsByName(compid.concat('_text'));
    var v = [];
    for (var i= 0; i < checs.length; i++){
        if (checs[i].checked ){
            v.push(checs[i].value);
        }
    }
    return JSON.stringify(v);
}

function getvisible(compid) {
    try{
        var txtContent = document.getElementById(compid).className;
        return txtContent.indexOf('nhidden')>-1 ? '0' :'1';
    }catch (e){} 
    return '0';
}

function getenable(compid) {
    try{
        var txtContent = document.getElementById(compid).className;
        return txtContent.indexOf(' ndisable')>-1 ? '0' :'1';
    }catch (e){} 
    return '0';
}
function gettypecomp(compid) {
    try{
        var txtContent = document.getElementById(compid).getAttribute("ntypecomp");
        return txtContent;
    }catch (e){} 
    return '';
}
function getfilecomp(compid) {
    try{
        var txtContent = document.getElementById(compid).getAttribute("nfilecomp");
        return txtContent;
    }catch (e){} 
    return '';
}


function getfname(f){
    if(f.length == 0) {
        return '';    
    } else {      
       if (f.indexOf('C:\\fakepath\\')>=0){
            f=f.substring(12);
         }
         return f;
    }
}

function documnetclientchange(f, id){
    if (getfname(f)==''){
        document.getElementById(id.concat('_finder')).style.display  = "";
        document.getElementById(id.concat('_upload')).style.display  = "none";
    }else{
        document.getElementById(id.concat('_finder')).style.display  = "none";
        document.getElementById(id.concat('_upload')).style.display  = "";
    }    
}


//resize
var cookie_namespace = 'nikita';  
function readCookie(cookie)  {
  var myCookie = cookie_namespace+"_"+cookie+"=";
  if (document.cookie) 
  {
    var index = document.cookie.indexOf(myCookie);
    if (index != -1) 
    {
      var valStart = index + myCookie.length;
      var valEnd = document.cookie.indexOf(";", valStart);
      if (valEnd == -1) 
      {
        valEnd = document.cookie.length;
      }
      var val = document.cookie.substring(valStart, valEnd);
      return val;
    }
  }
  return 0;
}

function writeCookie(cookie, val, expiration)  {
  if (val==undefined) return;
  if (expiration == null) 
  {
    var date = new Date();
    date.setTime(date.getTime()+(10*365*24*60*60*1000)); // default expiration is one week
    expiration = date.toGMTString();
  }
  document.cookie = cookie_namespace + "_" + cookie + "=" + val + "; expires=" + expiration+"; path=/";
}
 
function resizeWidth()  {
  var windowWidth = $(window).width() + "px";
  windowWidth = nWidth()+ "px";   
  var sidenavWidth = $(sidenav).outerWidth();
  
  content.css({marginLeft:parseInt(sidenavWidth)+"px"}); 
  writeCookie('width',sidenavWidth, null);
}

function restoreWidth(navWidth) {
  var windowWidth = $(window).width() + "px";
  
   windowWidth = nWidth()+ "px";   
   content.css({marginLeft:parseInt(navWidth)+6+"px"});
   sidenav.css({width:navWidth + "px"});
}
function nWidth(){
	var w = window.innerWidth
        || document.documentElement.clientWidth
        || document.body.clientWidth;
	return w;
}

function resizeHeight()  {
  var headerHeight = header.outerHeight();
  var footerHeight = footer.outerHeight();
  var windowHeight = $(window).height() - headerHeight - footerHeight;
  
   var h = window.innerHeight
        || document.documentElement.clientHeight
        || document.body.clientHeight;        
        
  windowHeight =h - headerHeight - footerHeight;
  
  content.css({height:windowHeight + "px"});
  navtree.css({height:windowHeight + "px"});
  sidenav.css({height:windowHeight + "px", top: headerHeight+"px"});

}
var header,footer,content,navtree,sidenav;
function initResizable() {
  header  = $(".nikita-header");
  sidenav = $(".nikita-side-nav");
  content = $(".nikita-content");
  navtree = $(".nikita-nav-menu");
  footer  = $(".nikita-footer");
  
  $(".side-nav-resizable").resizable({handles: 'e', resize: function(e, ui) { resizeWidth(); } });
  $(window).resize(function() { resizeHeight(); });
  
  var width = readCookie('width');
  
  if (width) { restoreWidth(width); } else { resizeWidth(); }
  resizeHeight();
}
//resize

function getElementsByClassName(cn){
    if(document.getElementsByClassName) // Returns NodeList here
        return document.getElementsByClassName(cn);

    cn = cn.replace(/ *$/, '');
    //if(document.querySelectorAll) // Returns NodeList here
        //return document.querySelectorAll((' ' + cn).replace(/ +/g, '.'));
    cn = cn.replace(/^ */, '');

    var classes = cn.split(/ +/), clength = classes.length;
    var els = document.getElementsByTagName('*'), elength = els.length;
    var results = [];
    var i, j, match;

    for(i = 0; i < elength; i++){
        match = true;
        for(j = clength; j--;)
            if(!RegExp(' ' + classes[j] + ' ').test(' ' + els[i].className + ' '))
                match = false;
        if(match)
            results.push(els[i]);
    }
    // Returns Array here
    return results;
}



if (typeof JSON !== 'object') {
    JSON = {};
}

(function () {
    'use strict';

    function f(n) {
        // Format integers to have at least two digits.
        return n < 10 ? '0' + n : n;
    }

    if (typeof Date.prototype.toJSON !== 'function') {

        Date.prototype.toJSON = function () {

            return isFinite(this.valueOf())
                ? this.getUTCFullYear()     + '-' +
                    f(this.getUTCMonth() + 1) + '-' +
                    f(this.getUTCDate())      + 'T' +
                    f(this.getUTCHours())     + ':' +
                    f(this.getUTCMinutes())   + ':' +
                    f(this.getUTCSeconds())   + 'Z'
                : null;
        };

        String.prototype.toJSON      =
            Number.prototype.toJSON  =
            Boolean.prototype.toJSON = function () {
                return this.valueOf();
            };
    }

    var cx,
        escapable,
        gap,
        indent,
        meta,
        rep;


    function quote(string) {

// If the string contains no control characters, no quote characters, and no
// backslash characters, then we can safely slap some quotes around it.
// Otherwise we must also replace the offending characters with safe escape
// sequences.

        escapable.lastIndex = 0;
        return escapable.test(string) ? '"' + string.replace(escapable, function (a) {
            var c = meta[a];
            return typeof c === 'string'
                ? c
                : '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
        }) + '"' : '"' + string + '"';
    }


    function str(key, holder) {

// Produce a string from holder[key].

        var i,          // The loop counter.
            k,          // The member key.
            v,          // The member value.
            length,
            mind = gap,
            partial,
            value = holder[key];

// If the value has a toJSON method, call it to obtain a replacement value.

        if (value && typeof value === 'object' &&
                typeof value.toJSON === 'function') {
            value = value.toJSON(key);
        }

// If we were called with a replacer function, then call the replacer to
// obtain a replacement value.

        if (typeof rep === 'function') {
            value = rep.call(holder, key, value);
        }

// What happens next depends on the value's type.

        switch (typeof value) {
        case 'string':
            return quote(value);

        case 'number':

// JSON numbers must be finite. Encode non-finite numbers as null.

            return isFinite(value) ? String(value) : 'null';

        case 'boolean':
        case 'null':

// If the value is a boolean or null, convert it to a string. Note:
// typeof null does not produce 'null'. The case is included here in
// the remote chance that this gets fixed someday.

            return String(value);

// If the type is 'object', we might be dealing with an object or an array or
// null.

        case 'object':

// Due to a specification blunder in ECMAScript, typeof null is 'object',
// so watch out for that case.

            if (!value) {
                return 'null';
            }

// Make an array to hold the partial results of stringifying this object value.

            gap += indent;
            partial = [];

// Is the value an array?

            if (Object.prototype.toString.apply(value) === '[object Array]') {

// The value is an array. Stringify every element. Use null as a placeholder
// for non-JSON values.

                length = value.length;
                for (i = 0; i < length; i += 1) {
                    partial[i] = str(i, value) || 'null';
                }

// Join all of the elements together, separated with commas, and wrap them in
// brackets.

                v = partial.length === 0
                    ? '[]'
                    : gap
                    ? '[\n' + gap + partial.join(',\n' + gap) + '\n' + mind + ']'
                    : '[' + partial.join(',') + ']';
                gap = mind;
                return v;
            }

// If the replacer is an array, use it to select the members to be stringified.

            if (rep && typeof rep === 'object') {
                length = rep.length;
                for (i = 0; i < length; i += 1) {
                    if (typeof rep[i] === 'string') {
                        k = rep[i];
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            } else {

// Otherwise, iterate through all of the keys in the object.

                for (k in value) {
                    if (Object.prototype.hasOwnProperty.call(value, k)) {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            }

// Join all of the member texts together, separated with commas,
// and wrap them in braces.

            v = partial.length === 0
                ? '{}'
                : gap
                ? '{\n' + gap + partial.join(',\n' + gap) + '\n' + mind + '}'
                : '{' + partial.join(',') + '}';
            gap = mind;
            return v;
        }
    }

// If the JSON object does not yet have a stringify method, give it one.

    if (typeof JSON.stringify !== 'function') {
        escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
        meta = {    // table of character substitutions
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"' : '\\"',
            '\\': '\\\\'
        };
        JSON.stringify = function (value, replacer, space) {

// The stringify method takes a value and an optional replacer, and an optional
// space parameter, and returns a JSON text. The replacer can be a function
// that can replace values, or an array of strings that will select the keys.
// A default replacer method can be provided. Use of the space parameter can
// produce text that is more easily readable.

            var i;
            gap = '';
            indent = '';

// If the space parameter is a number, make an indent string containing that
// many spaces.

            if (typeof space === 'number') {
                for (i = 0; i < space; i += 1) {
                    indent += ' ';
                }

// If the space parameter is a string, it will be used as the indent string.

            } else if (typeof space === 'string') {
                indent = space;
            }

// If there is a replacer, it must be a function or an array.
// Otherwise, throw an error.

            rep = replacer;
            if (replacer && typeof replacer !== 'function' &&
                    (typeof replacer !== 'object' ||
                    typeof replacer.length !== 'number')) {
                throw new Error('JSON.stringify');
            }

// Make a fake root object containing our value under the key of ''.
// Return the result of stringifying the value.

            return str('', {'': value});
        };
    }


// If the JSON object does not yet have a parse method, give it one.

    if (typeof JSON.parse !== 'function') {
        cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
        JSON.parse = function (text, reviver) {

// The parse method takes a text and an optional reviver function, and returns
// a JavaScript value if the text is a valid JSON text.

            var j;

            function walk(holder, key) {

// The walk method is used to recursively walk the resulting structure so
// that modifications can be made.

                var k, v, value = holder[key];
                if (value && typeof value === 'object') {
                    for (k in value) {
                        if (Object.prototype.hasOwnProperty.call(value, k)) {
                            v = walk(value, k);
                            if (v !== undefined) {
                                value[k] = v;
                            } else {
                                delete value[k];
                            }
                        }
                    }
                }
                return reviver.call(holder, key, value);
            }


// Parsing happens in four stages. In the first stage, we replace certain
// Unicode characters with escape sequences. JavaScript handles many characters
// incorrectly, either silently deleting them, or treating them as line endings.

            text = String(text);
            cx.lastIndex = 0;
            if (cx.test(text)) {
                text = text.replace(cx, function (a) {
                    return '\\u' +
                        ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
                });
            }

// In the second stage, we run the text against regular expressions that look
// for non-JSON patterns. We are especially concerned with '()' and 'new'
// because they can cause invocation, and '=' because it can cause mutation.
// But just to be safe, we want to reject all unexpected forms.

// We split the second stage into 4 regexp operations in order to work around
// crippling inefficiencies in IE's and Safari's regexp engines. First we
// replace the JSON backslash pairs with '@' (a non-JSON character). Second, we
// replace all simple value tokens with ']' characters. Third, we delete all
// open brackets that follow a colon or comma or that begin the text. Finally,
// we look to see that the remaining characters are only whitespace or ']' or
// ',' or ':' or '{' or '}'. If that is so, then the text is safe for eval.

            if (/^[\],:{}\s]*$/
                    .test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@')
                        .replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']')
                        .replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

// In the third stage we use the eval function to compile the text into a
// JavaScript structure. The '{' operator is subject to a syntactic ambiguity
// in JavaScript: it can begin a block or an object literal. We wrap the text
// in parens to eliminate the ambiguity.

                j = eval('(' + text + ')');

// In the optional fourth stage, we recursively walk the new structure, passing
// each name/value pair to a reviver function for possible transformation.

                return typeof reviver === 'function'
                    ? walk({'': j}, '')
                    : j;
            }

// If the text is not JSON parseable, then a SyntaxError is thrown.

            throw new SyntaxError('JSON.parse');
        };
    }
}());