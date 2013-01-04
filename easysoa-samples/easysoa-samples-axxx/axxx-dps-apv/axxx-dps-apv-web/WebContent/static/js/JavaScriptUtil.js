/*
 * JavaScriptUtil is a part of JavaScripTools (http://javascriptools.sourceforge.net).
 * This file was compressed using JavaScriptZip (http://javascriptzip.sourceforge.net).
 * Author: Luis Fernando Planella Gonzalez (lfpg.dev at gmail dot com)
 * Version: 2.2.3
 * JavaScripTools is distributed under the GNU Lesser General Public License (LGPL).
 * For more information, see http://www.gnu.org/licenses/lgpl-2.1.txt
*/
var JST_CHARS_NUMBERS="0123456789";var JST_CHARS_LOWER="";var JST_CHARS_UPPER="";for(var i=50;i<500;i++){var c=String.fromCharCode(i);var lower=c.toLowerCase();var upper=c.toUpperCase();if(lower!=upper){JST_CHARS_LOWER+=lower;JST_CHARS_UPPER+=upper}}
var JST_CHARS_LETTERS=JST_CHARS_LOWER+JST_CHARS_UPPER;var JST_CHARS_ALPHA=JST_CHARS_LETTERS+JST_CHARS_NUMBERS;var JST_CHARS_BASIC_LOWER="abcdefghijklmnopqrstuvwxyz";var JST_CHARS_BASIC_UPPER="ABCDEFGHIJKLMNOPQRSTUVWXYZ";var JST_CHARS_BASIC_LETTERS=JST_CHARS_BASIC_LOWER+JST_CHARS_BASIC_UPPER;var JST_CHARS_BASIC_ALPHA=JST_CHARS_BASIC_LETTERS+JST_CHARS_NUMBERS;var JST_CHARS_WHITESPACE=" \t\n\r";var MILLIS_IN_SECOND=1000;var MILLIS_IN_MINUTE=60*MILLIS_IN_SECOND;var MILLIS_IN_HOUR=60*MILLIS_IN_MINUTE;var MILLIS_IN_DAY=24*MILLIS_IN_HOUR;var JST_FIELD_MILLISECOND=0;var JST_FIELD_SECOND=1;var JST_FIELD_MINUTE=2;var JST_FIELD_HOUR=3;var JST_FIELD_DAY=4;var JST_FIELD_MONTH=5;var JST_FIELD_YEAR=6;
function getObject(objectName,source){if(isEmpty(objectName)){return null}
if(!isInstance(objectName,String)){return objectName}
if(isEmpty(source)){source=self}
if(isInstance(source,String)){sourceName=source;source=self.frames[sourceName];if(source==null) source=parent.frames[sourceName];if(source==null) source=top.frames[sourceName];if(source==null) source=getObject(sourceName);if(source==null) return null}
var document=(source.document)?source.document:source;if(document.getElementById){var collection=document.getElementsByName(objectName);if(collection.length==1) return collection[0];if(collection.length>1){if(typeof(collection)=="array"){return collection}
var ret=new Array(collection.length);for(var i=0;i<collection.length;i++){ret[i]=collection[i]}
return ret}
return document.getElementById(objectName)} else {if(document[objectName]) return document[objectName];if(document.all[objectName]) return document.all[objectName];if(source[objectName]) return source[objectName]}
return null}
function isInstance(object,clazz){if((object==null)||(clazz==null)){return false}
if(object instanceof clazz){return true}
if((clazz==String)&&(typeof(object)=="string")){return true}
if((clazz==Number)&&(typeof(object)=="number")){return true}
if((clazz==Array)&&(typeof(object)=="array")){return true}
if((clazz==Function)&&(typeof(object)=="function")){return true}
var base=object.base;while(base!=null){if(base==clazz){return true}
base=base.base}
return false}
function booleanValue(object,trueChars){if(object==true||object==false){return object} else {object=String(object);if(object.length==0){return false} else {var first=object.charAt(0).toUpperCase();trueChars=isEmpty(trueChars)?"T1YS":trueChars.toUpperCase();return trueChars.indexOf(first)!=-1
}}}
function isUndefined(object){return typeof(object)=="undefined"}
function invoke(functionName,args){var arguments;if(args==null||isUndefined(args)){arguments="()"} else if(!isInstance(args,Array)){arguments="(args)"} else {arguments="(";for(var i=0;i<args.length;i++){if(i>0){arguments+=","}
arguments+="args["+i+"]"}
arguments+=")"}
return eval(functionName+arguments)}
function invokeAsMethod(object,method,args){return method.apply(object,args)}
function ensureArray(object){if(typeof(object)=='undefined'||object==null){return []}
if(object instanceof Array){return object}
return [object]}
function indexOf(object,array,startingAt){if((object==null)||!(array instanceof Array)){return-1}
if(startingAt==null){startingAt=0}
for(var i=startingAt;i<array.length;i++){if(array[i]==object){return i}}
return-1}
function inArray(object,array){return indexOf(object,array)>=0}
function removeFromArray(array){if(!isInstance(array,Array)){return null}
var ret=[];var toRemove=removeFromArray.arguments.slice(1);for(var i=0;i<array.length;i++){var current=array[i];if(!inArray(current,toRemove)){ret[ret.length]=current}}
return ret}
function arrayConcat(){var ret=[];for(var i=0;i<arrayConcat.arguments.length;i++){var current=arrayConcat.arguments[i];if(!isEmpty(current)){if(!isInstance(current,Array)){current=[current]
}
for(j=0;j<current.length;j++){ret[ret.length]=current[j]}}}
return ret}
function arrayEquals(array1,array2){if(!isInstance(array1,Array)||!isInstance(array2,Array)){return false}
if(array1.length!=array2.length){return false}
for(var i=0;i<array1.length;i++){if(array1[i]!=array2[i]){return false}}
return true}
function checkAll(object,flag){if(typeof(object)=="string"){object=getObject(object)}
if(object!=null){if(!isInstance(object,Array)){object=[object]}
for(i=0;i<object.length;i++){object[i].checked=flag}}}
function observeEvent(object,eventName,handler){object=getObject(object);if(object!=null){if(object.addEventListener){object.addEventListener(eventName,function(e){return invokeAsMethod(object,handler,[e])},false)} else if(object.attachEvent){object.attachEvent("on"+eventName,function(){return invokeAsMethod(object,handler,[window.event])})} else {object["on"+eventName]=handler}}}
function typedCode(event){var code=0;if(event==null&&window.event){event=window.event}
if(event!=null){if(event.keyCode){code=event.keyCode} else if(event.which){code=event.which}}
return code}
function stopPropagation(event){if(event==null&&window.event){event=window.event}
if(event!=null){if(event.stopPropagation!=null){event.stopPropagation()} else if(event.cancelBubble!==null){event.cancelBubble=true}}
return false}
function preventDefault(event){if(event==null&&window.event){event=window.event}
if(event!=null){if(event.preventDefault!=null){event.preventDefault()} else if(event.returnValue!==null){event.returnValue=false}}
return false}
function prepareForCaret(object){object=getObject(object);if(object==null||!object.type){return null}
if(object.createTextRange){var handler=function(){object.caret=document.selection.createRange().duplicate()}
object.attachEvent("onclick",handler);object.attachEvent("ondblclick",handler);object.attachEvent("onselect",handler);object.attachEvent("onkeyup",handler)}}
function isCaretSupported(object){object=getObject(object);if(object==null||!object.type){return false}
if(navigator.userAgent.toLowerCase().indexOf("opera")>=0){return false}
return object.setSelectionRange!=null||object.createTextRange!=null}
function isInputSelectionSupported(object){object=getObject(object);if(object==null||!object.type){return false}
return object.setSelectionRange!=null||object.createTextRange!=null}
function getInputSelection(object){object=getObject(object);if(object==null||!object.type){return null}
if(object.createTextRange&&object.caret){return object.caret.text} else if(object.setSelectionRange){var selStart=object.selectionStart;var selEnd=object.selectionEnd;return object.value.substring(selStart,selEnd)}
return ""}
function getInputSelectionRange(object){object=getObject(object);if(object==null||!object.type){return null}
if(object.selectionEnd){return [object.selectionStart,object.selectionEnd]} else if(object.createTextRange&&object.caret){var end=getCaret(object);return [end-object.caret.text.length,end]}
return null}
function setInputSelectionRange(object,start,end){object=getObject(object);if(object==null||!object.type){return}
if(start<0){start=0}
if(end>object.value.length){end=object.value.length}
if(object.setSelectionRange){object.focus();object.setSelectionRange(start,end)} else if(object.createTextRange){object.focus();var range;if(object.caret){range=object.caret;range.moveStart("textedit",-1);range.moveEnd("textedit",-1)} else {range=object.createTextRange()}
range.moveEnd('character',end);range.moveStart('character',start);range.select()}}
function getCaret(object){object=getObject(object);if(object==null||!object.type){return null}
try {if(object.createTextRange&&object.caret){var range=object.caret.duplicate();range.moveStart('textedit',-1);return range.text.length} else if(object.selectionStart||object.selectionStart==0){return object.selectionStart}} catch(e){}
return null}
function setCaret(object,pos){setInputSelectionRange(object,pos,pos)}
function setCaretToEnd(object){object=getObject(object);if(object==null||!object.type){return}
if(object.createTextRange){var range=object.createTextRange();range.collapse(false);range.select()} else if(object.setSelectionRange){var length=object.value.length;object.setSelectionRange(length,length);object.focus()}}
function setCaretToStart(object){object=getObject(object);if(object==null||!object.type){return}
if(object.createTextRange){var range=object.createTextRange();range.collapse(true);range.select()} else if(object.setSelectionRange){object.focus();object.setSelectionRange(0,0)}}
function selectString(object,string){if(isInstance(object,String)){object=getObject(object)}
if(object==null||!object.type){return}
var match=new RegExp(string,"i").exec(object.value);if(match){setInputSelectionRange(object,match.index,match.index+match[0].length)}}
function replaceSelection(object,string){object=getObject(object);if(object==null||!object.type){return}
if(object.setSelectionRange){var selectionStart=object.selectionStart;var selectionEnd=object.selectionEnd;object.value=object.value.substring(0,selectionStart)+string+object.value.substring(selectionEnd);if(selectionStart!=selectionEnd){setInputSelectionRange(object,selectionStart,selectionStart+string.length)} else {setCaret(object,selectionStart+string.length)}} else if(object.createTextRange&&object.caret){object.caret.text=string}}
function clearOptions(select){select=getObject(select);var ret=[];if(select!=null){for(var i=0;i<select.options.length;i++){var option=select.options[i];ret[ret.length]=new Option(option.text,option.value)}
select.options.length=0}
return ret}
function addOption(select,option,sort,textProperty,valueProperty,selectedProperty){select=getObject(select);if(select==null||option==null){return}
textProperty=textProperty||"text";valueProperty=valueProperty||"value";selectedProperty=selectedProperty||"selected"
if(isInstance(option,Map)){option=option.toObject()}
if(isUndefined(option[valueProperty])){valueProperty=textProperty}
var selected=false;if(!isUndefined(option[selectedProperty])){selected=option[selectedProperty]}
option=new Option(option[textProperty],option[valueProperty],selected,selected);select.options[select.options.length]=option;if(booleanValue(sort)){sortOptions(select)}}
function addOptions(select,options,sort,textProperty,valueProperty,selectedProperty){select=getObject(select);if(select==null){return}
for(var i=0;i<options.length;i++){addOption(select,options[i],false,textProperty,valueProperty,selectedProperty)}
if(!select.multiple&&select.selectedIndex<0&&select.options.length>0){select.selectedIndex=0}
if(booleanValue(sort)){sortOptions(select)}}
function compareOptions(opt1,opt2){if(opt1==null&&opt2==null){return 0}
if(opt1==null){return-1}
if(opt2==null){return 1}
if(opt1.text==opt2.text){return 0} else if(opt1.text>opt2.text){return 1} else {return-1}}
function setOptions(select,options,addEmpty,sort,textProperty,valueProperty,selectedProperty){select=getObject(select);var ret=clearOptions(select);var addEmptyIsString=isInstance(addEmpty,String);if(addEmptyIsString||booleanValue(addEmpty)){select.options[0]=new Option(addEmptyIsString?addEmpty:"")}
addOptions(select,options,sort,textProperty,valueProperty,selectedProperty);return ret}
function sortOptions(select,sortFunction){select=getObject(select);if(select==null){return}
var options=clearOptions(select);if(isInstance(sortFunction,Function)){options.sort(sortFunction)} else {options.sort(compareOptions)}
setOptions(select,options)}
function transferOptions(source,dest,all,sort){source=getObject(source);dest=getObject(dest);if(source==null||dest==null){return}
if(booleanValue(all)){addOptions(dest,clearOptions(source),sort)} else {var sourceOptions=[];var destOptions=[];for(var i=0;i<source.options.length;i++){var option=source.options[i];var options=(option.selected)?destOptions:sourceOptions;options[options.length]=new Option(option.text,option.value)}
setOptions(source,sourceOptions,false,sort);addOptions(dest,destOptions,sort)}}
function getValue(object){object=getObject(object);if(object==null){return null}
if(object.length&&!object.type){var ret=[];for(var i=0;i<object.length;i++){var temp=getValue(object[i]);if(temp!=null){ret[ret.length]=temp}}
return ret.length==0?null:ret.length==1?ret[0]:ret}
if(object.type){if(object.type.indexOf("select")>=0){var ret=[];if(!object.multiple&&object.selectedIndex<0&&object.options.length>0){ret[ret.length]=object.options[0].value} else {for(i=0;i<object.options.length;i++){if(booleanValue(object.options[i].selected)){ret[ret.length]=object[i].value;if(!object.multiple){break}}}}
return ret.length==0?null:ret.length==1?ret[0]:ret}
if(object.type=="radio"||object.type=="checkbox"){return booleanValue(object.checked)?object.value:null} else {return object.value}} else if(typeof(object.innerHTML)!="undefined"){return object.innerHTML} else {return null}}
function setValue(object,value){if(object==null){return}
if(typeof(object)=="string"){object=getObject(object)}
var values=ensureArray(value);for(var i=0;i<values.length;i++){values[i]=values[i]==null?"":""+values[i]}
if(object.length&&!object.type){while(values.length<object.length){values[values.length]=""}
for(var i=0;i<object.length;i++){var obj=object[i];setValue(obj,inArray(obj.type,["checkbox","radio"])?values:values[i])}
return}
if(object.type){if(object.type.indexOf("select")>=0){for(var i=0;i<object.options.length;i++){var option=object.options[i];option.selected=inArray(option.value,values)}
return} else if(object.type=="radio"||object.type=="checkbox"){object.checked=inArray(object.value,values);return} else {object.value=values.length==0?"":values[0];return}} else if(typeof(object.innerHTML)!="undefined"){object.innerHTML=values.length==0?"":values[0]}}
function decode(object){var args=decode.arguments;for(var i=1;i<args.length;i+=2){if(i<args.length-1){if(args[i]==object){return args[i+1]}} else {return args[i]}}
return null}
function select(){var args=select.arguments;for(var i=0;i<args.length;i+=2){if(i<args.length-1){if(booleanValue(args[i])){return args[i+1]}} else {return args[i]}}
return null}
function isEmpty(object){return object==null||String(object)==""||typeof(object)=="undefined"||(typeof(object)=="number"&&isNaN(object))}
function ifEmpty(object,emptyValue){return isEmpty(object)?emptyValue:object}
function ifNull(object,nullValue){return object==null?nullValue:object}
function replaceAll(string,find,replace){return String(string).split(find).join(replace)}
function repeat(string,times){var ret="";for(var i=0;i<Number(times);i++){ret+=string}
return ret}
function ltrim(string,chars){string=string?String(string):"";chars=chars||JST_CHARS_WHITESPACE;var pos=0;while(chars.indexOf(string.charAt(pos))>=0&&(pos<=string.length)){pos++}
return string.substr(pos)}
function rtrim(string,chars){string=string?String(string):"";chars=chars||JST_CHARS_WHITESPACE;var pos=string.length-1;while(chars.indexOf(string.charAt(pos))>=0&&(pos>=0)){pos--}
return string.substring(0,pos+1)}
function trim(string,chars){chars=chars||JST_CHARS_WHITESPACE;return ltrim(rtrim(string,chars),chars)}
function lpad(string,size,chr){string=String(string);if(size<0){return ""}
if(isEmpty(chr)){chr=" "} else {chr=String(chr).charAt(0)}
while(string.length<size){string=chr+string}
return left(string,size)}
function rpad(string,size,chr){string=String(string);if(size<=0){return ""}
chr=String(chr);if(isEmpty(chr)){chr=" "} else {chr=chr.charAt(0)}
while(string.length<size){string+=chr}
return left(string,size)}
function crop(string,pos,size){string=String(string);if(size==null){size=1}
if(size<=0){return ""}
return left(string,pos)+mid(string,pos+size)}
function lcrop(string,size){if(size==null){size=1}
return crop(string,0,size)}
function rcrop(string,size){string=String(string);if(size==null){size=1}
return crop(string,string.length-size,size)}
function capitalize(text,separators){text=String(text);separators=separators||JST_CHARS_WHITESPACE+'.?!';var out="";var last='';for(var i=0;i<text.length;i++){var current=text.charAt(i);if(separators.indexOf(last)>=0){out+=current.toUpperCase()} else {out+=current.toLowerCase()}
last=current}
return out}
function onlySpecified(string,possible){string=String(string);possible=String(possible);for(var i=0;i<string.length;i++){if(possible.indexOf(string.charAt(i))==-1){return false}}
return true}
function onlyNumbers(string){return onlySpecified(string,JST_CHARS_NUMBERS)}
function onlyLetters(string){return onlySpecified(string,JST_CHARS_LETTERS)}
function onlyAlpha(string){return onlySpecified(string,JST_CHARS_ALPHA)}
function onlyBasicLetters(string){return onlySpecified(string,JST_CHARS_BASIC_LETTERS)}
function onlyBasicAlpha(string){return onlySpecified(string,JST_CHARS_BASIC_ALPHA)}
function left(string,n){string=String(string);return string.substring(0,n)}
function right(string,n){string=String(string);return string.substr(string.length-n)}
function mid(string,pos,n){string=String(string);if(n==null){n=string.length}
return string.substring(pos,pos+n)}
function insertString(string,pos,value){string=String(string);var prefix=left(string,pos);var suffix=mid(string,pos)
return prefix+value+suffix}
function functionName(funct,unnamed){if(typeof(funct)=="function"){var src=funct.toString();var start=src.indexOf("function");var end=src.indexOf("(");if((start>=0)&&(end>=0)){start+=8;var name=trim(src.substring(start,end));return isEmpty(name)?(unnamed||"[unnamed]"):name}} if(typeof(funct)=="object"){return functionName(funct.constructor)}
return null}
function debug(object,separator,sort,includeObject,objectSeparator){if(object==null){return "null"}
sort=booleanValue(sort==null?true:sort);includeObject=booleanValue(includeObject==null?true:sort);separator=separator||"\n";objectSeparator=objectSeparator||"--------------------";var properties=[];for(var property in object){var part=property+" = ";try {part+=object[property]} catch(e){part+="<Error retrieving value>"}
properties[properties.length]=part}
if(sort){properties.sort()}
var out="";if(includeObject){try {out=object.toString()+separator} catch(e){out="<Error calling the toString() method>"
}
if(!isEmpty(objectSeparator)){out+=objectSeparator+separator}}
out+=properties.join(separator);return out}
function escapeCharacters(string,extraChars,onlyExtra){var ret=String(string);extraChars=String(extraChars||"");onlyExtra=booleanValue(onlyExtra);if(!onlyExtra){ret=replaceAll(ret,"\n","\\n");ret=replaceAll(ret,"\r","\\r");ret=replaceAll(ret,"\t","\\t");ret=replaceAll(ret,"\"","\\\"");ret=replaceAll(ret,"\'","\\\'");ret=replaceAll(ret,"\\","\\\\")}
for(var i=0;i<extraChars.length;i++){var chr=extraChars.charAt(i);ret=replaceAll(ret,chr,"\\\\u"+lpad(new Number(chr.charCodeAt(0)).toString(16),4,'0'))}
return ret}
function unescapeCharacters(string,onlyExtra){var ret=String(string);var pos=-1;var u="\\\\u";onlyExtra=booleanValue(onlyExtra);do {pos=ret.indexOf(u);if(pos>=0){var charCode=parseInt(ret.substring(pos+u.length,pos+u.length+4),16);ret=replaceAll(ret,u+charCode,String.fromCharCode(charCode))}} while(pos>=0);if(!onlyExtra){ret=replaceAll(ret,"\\n","\n");ret=replaceAll(ret,"\\r","\r");ret=replaceAll(ret,"\\t","\t");ret=replaceAll(ret,"\\\"", "\"");ret=replaceAll(ret,"\\\'","\'");ret=replaceAll(ret,"\\\\","\\")}
return ret}
function writeCookie(name,value,document,expires,path,domain,secure){document=document||self.document;var str=name+"="+(isEmpty(value)?"":encodeURIComponent(value));if(path!=null) str+="; path="+path;if(domain!=null) str+="; domain="+domain;if(secure!=null&&booleanValue(secure)) str+="; secure";if(expires===false) expires=new Date(2500,12,31);if(expires instanceof Date) str+="; expires="+expires.toGMTString();document.cookie=str}
function readCookie(name,document){document=document||self.document;var prefix=name+"=";var cookie=document.cookie;var begin=cookie.indexOf("; "+prefix);if(begin==-1){begin=cookie.indexOf(prefix);if(begin!=0) return null} else
begin+=2;var end=cookie.indexOf(";",begin);if(end==-1)
end=cookie.length;return decodeURIComponent(cookie.substring(begin+prefix.length,end))}
function deleteCookie(name,document,path,domain){writeCookie(name,null,document,path,domain)}
function getDateField(date,field){if(!isInstance(date,Date)){return null}
switch(field){case JST_FIELD_MILLISECOND:return date.getMilliseconds();case JST_FIELD_SECOND:return date.getSeconds();case JST_FIELD_MINUTE:return date.getMinutes();case JST_FIELD_HOUR:return date.getHours();case JST_FIELD_DAY:return date.getDate();case JST_FIELD_MONTH:return date.getMonth();case JST_FIELD_YEAR:return date.getFullYear()}
return null}
function setDateField(date,field,value){if(!isInstance(date,Date)){return}
switch(field){case JST_FIELD_MILLISECOND:date.setMilliseconds(value);break;case JST_FIELD_SECOND:date.setSeconds(value);break;case JST_FIELD_MINUTE:date.setMinutes(value);break;case JST_FIELD_HOUR:date.setHours(value);break;case JST_FIELD_DAY:date.setDate(value);break;case JST_FIELD_MONTH:date.setMonth(value);break;case JST_FIELD_YEAR:date.setFullYear(value);break}}
function dateAdd(date,amount,field){if(!isInstance(date,Date)){return null}
if(amount==0){return new Date(date.getTime())}
if(!isInstance(amount,Number)){amount=1}
if(field==null) field=JST_FIELD_DAY;if(field<0||field>JST_FIELD_YEAR){return null}
var time=date.getTime();if(field<=JST_FIELD_DAY){var mult=1;switch(field){case JST_FIELD_SECOND:mult=MILLIS_IN_SECOND;break;case JST_FIELD_MINUTE:mult=MILLIS_IN_MINUTE;break;case JST_FIELD_HOUR:mult=MILLIS_IN_HOUR;break;case JST_FIELD_DAY:mult=MILLIS_IN_DAY;break}
var time=date.getTime();time+=mult*amount;return new Date(time)}
var ret=new Date(time);var day=ret.getDate();var month=ret.getMonth();var year=ret.getFullYear();if(field==JST_FIELD_YEAR){year+=amount} else if(field==JST_FIELD_MONTH){month+=amount}
while(month>11){month-=12;year++}
day=Math.min(day,getMaxDay(month,year));ret.setDate(day);ret.setMonth(month);ret.setFullYear(year);return ret}
function dateDiff(date1,date2,field){if(!isInstance(date1,Date)||!isInstance(date2,Date)){return null}
if(field==null) field=JST_FIELD_DAY;if(field<0||field>JST_FIELD_YEAR){return null}
if(field<=JST_FIELD_DAY){var div=1;switch(field){case JST_FIELD_SECOND:div=MILLIS_IN_SECOND;break;case JST_FIELD_MINUTE:div=MILLIS_IN_MINUTE;break;case JST_FIELD_HOUR:div=MILLIS_IN_HOUR;break;case JST_FIELD_DAY:div=MILLIS_IN_DAY;break}
return Math.round((date2.getTime()-date1.getTime())/div)}
var years=date2.getFullYear()-date1.getFullYear();if(field==JST_FIELD_YEAR){return years} else if(field==JST_FIELD_MONTH){var months1=date1.getMonth();var months2=date2.getMonth();if(years<0){months1+=Math.abs(years)*12} else if(years>0){months2+=years*12}
return(months2-months1)}
return null}
function truncDate(date,field){if(!isInstance(date,Date)){return null}
if(field==null) field=JST_FIELD_DAY;if(field<0||field>JST_FIELD_YEAR){return null}
var ret=new Date(date.getTime());if(field>JST_FIELD_MILLISECOND){ret.setMilliseconds(0)}
if(field>JST_FIELD_SECOND){ret.setSeconds(0)}
if(field>JST_FIELD_MINUTE){ret.setMinutes(0)}
if(field>JST_FIELD_HOUR){ret.setHours(0)}
if(field>JST_FIELD_DAY){ret.setDate(1)}
if(field>JST_FIELD_MONTH){ret.setMonth(0)}
return ret}
function getMaxDay(month,year){month=new Number(month)+1;year=new Number(year);switch(month){case 1:case 3:case 5:case 7:case 8:case 10:case 12:return 31;case 4:case 6:case 9:case 11:return 30;case 2:if((year%4)==0){return 29} else {return 28}
default:return 0}}
function getFullYear(year){year=Number(year);if(year<1000){if(year<50||year>100){year+=2000} else {year+=1900}}
return year}
function setOpacity(object,value){object=getObject(object);if(object==null){return}
value=Math.round(Number(value));if(isNaN(value)||value>100){value=100}
if(value<0){value=0}
var style=object.style;if(style==null){return}
style.MozOpacity=value/100;style.filter="alpha(opacity="+value+")"}
function getOpacity(object){object=getObject(object);if(object==null){return}
var style=object.style;if(style==null){return}
if(style.MozOpacity){return Math.round(style.MozOpacity*100)} else if(style.filter){var regExp=new RegExp("alpha\\(opacity=(\d*)\\)");var array=regExp.exec(style.filter);if(array!=null&&array.length>1){return parseInt(array[1],10)}}
return 100}
function Pair(key,value){this.key=key==null?"":key;this.value=value;
this.toString=function(){return this.key+"="+this.value}}
function Value(key,value){this.base=Pair;this.base(key,value)}
function Map(pairs){this.pairs=pairs||[];this.afterSet=null;this.afterRemove=null;
this.putValue=function(pair){this.putPair(pair)}
this.putPair=function(pair){if(isInstance(pair,Pair)){for(var i=0;i<this.pairs.length;i++){if(this.pairs[i].key==pair.key){this.pairs[i].value=pair.value}}
this.pairs[this.pairs.length]=pair;if(this.afterSet!=null){this.afterSet(pair,this)}}}
this.put=function(key,value){this.putValue(new Pair(key,value))}
this.putAll=function(map){if(!(map instanceof Map)){return}
var entries=map.getEntries();for(var i=0;i<entries.length;i++){this.putPair(entries[i])}}
this.size=function(){return this.pairs.length}
this.get=function(key){for(var i=0;i<this.pairs.length;i++){var pair=this.pairs[i];if(pair.key==key){return pair.value}}
return null}
this.getKeys=function(){var ret=[];for(var i=0;i<this.pairs.length;i++){ret[ret.length]=this.pairs[i].key}
return ret}
this.getValues=function(){var ret=[];for(var i=0;i<this.pairs.length;i++){ret[ret.length]=this.pairs[i].value}
return ret}
this.getEntries=function(){return this.getPairs()}
this.getPairs=function(){var ret=[];for(var i=0;i<this.pairs.length;i++){ret[ret.length]=this.pairs[i]}
return ret}
this.remove=function(key){for(var i=0;i<this.pairs.length;i++){var pair=this.pairs[i];if(pair.key==key){this.pairs.splice(i,1);if(this.afterRemove!=null){this.afterRemove(pair,this)}
return pair}}
return null}
this.clear=function(key){var ret=this.pairs;for(var i=0;i<ret.length;i++){this.remove(ret[i].key)}
return ret}
this.toString=function(){return functionName(this.constructor)+": {"+this.pairs+"}"}
this.toObject=function(){ret={};for(var i=0;i<this.pairs.length;i++){var pair=this.pairs[i];ret[pair.key]=pair.value}
return ret}}
function StringMap(string,nameSeparator,valueSeparator,isEncoded){this.nameSeparator=nameSeparator||"&";this.valueSeparator=valueSeparator||"=";this.isEncoded=isEncoded==null?true:booleanValue(isEncoded);var pairs=[];string=trim(string);if(!isEmpty(string)){var namesValues=string.split(nameSeparator);for(i=0;i<namesValues.length;i++){var nameValue=namesValues[i].split(valueSeparator);var name=trim(nameValue[0]);var value="";if(nameValue.length>0){value=trim(nameValue[1]);if(this.isEncoded){value=decodeURIComponent(value)}}
var pos=-1;for(j=0;j<pairs.length;j++){if(pairs[j].key==name){pos=j;break}}
if(pos>=0){var array=pairs[pos].value;if(!isInstance(array,Array)){array=[array]}
array[array.length]=value;pairs[pos].value=array} else {pairs[pairs.length]=new Pair(name,value)}}}
this.base=Map;this.base(pairs);
this.getString=function(){var ret=[];for(var i=0;i<this.pairs.length;i++){var pair=this.pairs[i];ret[ret.length]=pair.key+this.valueSeparator+this.value}
return ret.join(this.nameSeparator)}}
function QueryStringMap(location){this.location=location||self.location;var string=String(this.location.search);if(!isEmpty(string)){string=string.substr(1)}
this.base=StringMap;this.base(string,"&","=",true);this.putPair=function(){alert("Cannot put a value on a query string")}
this.remove=function(){alert("Cannot remove a value from a query string")}}
function CookieMap(document){this.document=document||self.document;this.base=StringMap;this.base(document.cookie,";","=",true);this.afterSet=function(pair){writeCookie(pair.key,pair.value,this.document)}
this.afterRemove=function(pair){deleteCookie(pair.key,this.document)}}
function ObjectMap(object){this.object=object;var pairs=[];for(var property in this.object){pairs[pairs.length]=new Pair(property,this.object[property])}
this.base=Map;this.base(pairs);this.afterSet=function(pair){this.object[pair.key]=pair.value}
this.afterRemove=function(pair){try {delete object[pair.key]} catch(exception){object[pair.key]=null}}}
function StringBuffer(initialCapacity){this.initialCapacity=initialCapacity||10;this.buffer=new Array(this.initialCapacity);this.append=function(value){this.buffer[this.buffer.length]=value;return this}
this.clear=function(){delete this.buffer;this.buffer=new Array(this.initialCapacity)}
this.toString=function(){return this.buffer.join("")}
this.length=function(){return this.toString().length}}