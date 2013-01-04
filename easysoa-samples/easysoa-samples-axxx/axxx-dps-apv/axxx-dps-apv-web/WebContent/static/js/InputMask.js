/*
 * InputMask is a part of JavaScripTools (http://javascriptools.sourceforge.net).
 * This file was compressed using JavaScriptZip (http://javascriptzip.sourceforge.net).
 * Author: Luis Fernando Planella Gonzalez (lfpg.dev at gmail dot com)
 * Version: 2.2.3
 * JavaScripTools is distributed under the GNU Lesser General Public License (LGPL).
 * For more information, see http://www.gnu.org/licenses/lgpl-2.1.txt
*/
var JST_NUMBER_MASK_APPLY_ON_BACKSPACE=true;var JST_MASK_VALIDATE_ON_BLUR=true;var JST_DEFAULT_ALLOW_NEGATIVE=true;var JST_DEFAULT_LEFT_TO_RIGHT=false;var JST_DEFAULT_DATE_MASK_VALIDATE=true;var JST_DEFAULT_DATE_MASK_VALIDATION_MESSAGE="";var JST_DEFAULT_DATE_MASK_YEAR_PAD_FUNCTION=getFullYear;var JST_DEFAULT_DATE_MASK_AM_PM_PAD_FUNCTION=function(value){if(isEmpty(value)) return "";switch(left(value,1)){case 'a':return 'am';case 'A':return 'AM';case 'p':return 'pm';case 'P':return 'PM'}
return value}
var JST_FIELD_DECIMAL_SEPARATOR=new Literal(typeof(JST_DEFAULT_DECIMAL_SEPARATOR)=="undefined"?",":JST_DEFAULT_DECIMAL_SEPARATOR);var JST_DEFAULT_LIMIT_OUTPUT_TEXT="${left}";numbers=new Input(JST_CHARS_NUMBERS);optionalNumbers=new Input(JST_CHARS_NUMBERS);optionalNumbers.optional=true;oneToTwoNumbers=new Input(JST_CHARS_NUMBERS,1,2);year=new Input(JST_CHARS_NUMBERS,1,4,getFullYear);dateSep=new Literal("/");dateTimeSep=new Literal(" ");timeSep=new Literal(":");
var JST_MASK_NUMBERS=[numbers];var JST_MASK_DECIMAL=[numbers,JST_FIELD_DECIMAL_SEPARATOR,optionalNumbers];var JST_MASK_UPPER=[new Upper(JST_CHARS_LETTERS)];var JST_MASK_LOWER=[new Lower(JST_CHARS_LETTERS)];var JST_MASK_CAPITALIZE=[new Capitalize(JST_CHARS_LETTERS)];var JST_MASK_LETTERS=[new Input(JST_CHARS_LETTERS)];var JST_MASK_ALPHA=[new Input(JST_CHARS_ALPHA)];var JST_MASK_ALPHA_UPPER=[new Upper(JST_CHARS_ALPHA)];var JST_MASK_ALPHA_LOWER=[new Lower(JST_CHARS_ALPHA)];var JST_MASK_DATE=[oneToTwoNumbers,dateSep,oneToTwoNumbers,dateSep,year];var JST_MASK_DATE_TIME=[oneToTwoNumbers,dateSep,oneToTwoNumbers,dateSep,year,dateTimeSep,oneToTwoNumbers,timeSep,oneToTwoNumbers];var JST_MASK_DATE_TIME_SEC=[oneToTwoNumbers,dateSep,oneToTwoNumbers,dateSep,year,dateTimeSep,oneToTwoNumbers,timeSep,oneToTwoNumbers,timeSep,oneToTwoNumbers];delete numbers;delete optionalNumbers;delete oneToTwoNumbers;delete year;delete dateSep;delete dateTimeSep;delete timeSep;
var JST_IGNORED_KEY_CODES=[45,35,36,33,34,37,39,38,40,127,4098];if(navigator.userAgent.toLowerCase().indexOf("khtml")<0){JST_IGNORED_KEY_CODES[JST_IGNORED_KEY_CODES.length]=46}
for(var i=0;i<32;i++){JST_IGNORED_KEY_CODES[JST_IGNORED_KEY_CODES.length]=i}
for(var i=112;i<=123;i++){JST_IGNORED_KEY_CODES[JST_IGNORED_KEY_CODES.length]=i}
function InputMask(fields,control,keyPressFunction,keyDownFunction,keyUpFunction,blurFunction,updateFunction,changeFunction){if(isInstance(fields,String)){fields=maskBuilder.parse(fields)} else if(isInstance(fields,MaskField)){fields=[fields]}
if(isInstance(fields,Array)){for(var i=0;i<fields.length;i++){var field=fields[i];if(!isInstance(field,MaskField)){alert("Invalid field: "+field);return}}} else {alert("Invalid field array: "+fields);return}
this.fields=fields;control=validateControlToMask(control);if(!control){alert("Invalid control to mask");return} else {this.control=control;prepareForCaret(this.control);this.control.supportsCaret=isCaretSupported(this.control)}
this.control.mask=this;this.control.pad=false;this.control.ignore=false;this.keyDownFunction=keyDownFunction||null;this.keyPressFunction=keyPressFunction||null;this.keyUpFunction=keyUpFunction||null;this.blurFunction=blurFunction||null;this.updateFunction=updateFunction||null;this.changeFunction=changeFunction||null;function onKeyDown(event){if(window.event){event=window.event}
this.keyCode=typedCode(event);if(this.mask.keyDownFunction!=null){var ret=invokeAsMethod(this,this.mask.keyDownFunction,[event,this.mask]);if(ret==false){return preventDefault(event)}}}
observeEvent(this.control,"keydown",onKeyDown);function onKeyPress(event){if(window.event){event=window.event}
var keyCode=this.keyCode||typedCode(event);var ignore=event.altKey||event.ctrlKey||inArray(keyCode,JST_IGNORED_KEY_CODES);if(!ignore){var range=getInputSelectionRange(this);if(range!=null&&range[0]!=range[1]){replaceSelection(this,"")}}
this.caretPosition=getCaret(this);this.setFixedLiteral=null;var typedChar=this.typedChar=ignore?"":String.fromCharCode(typedCode(event));var fieldDescriptors=this.fieldDescriptors=this.mask.getCurrentFields();var currentFieldIndex=this.currentFieldIndex=this.mask.getFieldIndexUnderCaret();var accepted=false;if(!ignore){var currentField=this.mask.fields[currentFieldIndex];accepted=currentField.isAccepted(typedChar);if(accepted){if(currentField.upper){typedChar=this.typedChar=typedChar.toUpperCase()} else if(currentField.lower){typedChar=this.typedChar=typedChar.toLowerCase()}} else {var previousFieldIndex=currentFieldIndex-1;if(currentFieldIndex>0&&this.mask.fields[previousFieldIndex].literal&&isEmpty(fieldDescriptors[previousFieldIndex].value)){this.setFixedLiteral=previousFieldIndex;accepted=true} else if(currentFieldIndex<this.mask.fields.length-1){var descriptor=fieldDescriptors[currentFieldIndex];var nextFieldIndex=currentFieldIndex+1;var nextField=this.mask.fields[nextFieldIndex];if(nextField.literal&&nextField.text.indexOf(typedChar)>=0){this.setFixedLiteral=nextFieldIndex;accepted=true}}}}
if(this.mask.keyPressFunction!=null){var ret=invokeAsMethod(this,this.mask.keyPressFunction,[event,this.mask]);if(ret==false){return preventDefault(event)}}
if(ignore){return}
var shouldApplyMask=!ignore&&accepted;if(shouldApplyMask){applyMask(this.mask,false)}
this.keyCode=null;return preventDefault(event)}
observeEvent(this.control,"keypress",onKeyPress);function onKeyUp(event){if(window.event){event=window.event}
if(this.mask.keyUpFunction!=null){var ret=invokeAsMethod(this,this.mask.keyUpFunction,[event,this.mask]);if(ret==false){return preventDefault(event)}}}
observeEvent(this.control,"keyup",onKeyUp);function onFocus(event){this._lastValue=this.value}
observeEvent(this.control,"focus",onFocus);function onBlur(event){if(window.event){event=window.event}
document.fieldOnBlur=this;try {var valueChanged=this._lastValue!=this.value;if(valueChanged&&JST_MASK_VALIDATE_ON_BLUR){applyMask(this.mask,true)}
if(this.mask.changeFunction!=null){if(valueChanged&&this.mask.changeFunction!=null){var e={};for(property in event){e[property]=event[property]}
e.type="change";invokeAsMethod(this,this.mask.changeFunction,[e,this.mask])}}
if(this.mask.blurFunction!=null){var ret=invokeAsMethod(this,this.mask.blurFunction,[event,this.mask]);if(ret==false){return preventDefault(event)}}
return true} finally {document.fieldOnBlur=null}}
observeEvent(this.control,"blur",onBlur);this.isComplete=function(){applyMask(this,true);var descriptors=this.getCurrentFields();if(descriptors==null||descriptors.length==0){return false}
for(var i=0;i<this.fields.length;i++){var field=this.fields[i];if(field.input&&!field.isComplete(descriptors[i].value)&&!field.optional){return false}}
return true}
this.update=function(){applyMask(this,true)}
this.getCurrentFields=function(value){value=value||this.control.value;var descriptors=[];var currentIndex=0;var lastInputIndex=-1;for(var i=0;i<this.fields.length;i++){var field=this.fields[i];var fieldValue="";var descriptor={};if(field.literal){if(lastInputIndex>=0){var lastInputField=this.fields[lastInputIndex];var lastInputDescriptor=descriptors[lastInputIndex];if(field.text.indexOf(mid(value,currentIndex,1))<0&&lastInputField.acceptsMoreText(lastInputDescriptor.value)){descriptor.begin=-1} else {descriptor.begin=currentIndex}}
if(currentIndex>=value.length){break}
if(value.substring(currentIndex,currentIndex+field.text.length)==field.text){currentIndex+=field.text.length}} else {var upTo=field.upTo(value,currentIndex);if(upTo<0&&currentIndex>=value.length){break}
fieldValue=upTo<0?"":field.transformValue(value.substring(currentIndex,upTo+1));descriptor.begin=currentIndex;descriptor.value=fieldValue;currentIndex+=fieldValue.length;lastInputIndex=i}
descriptors[i]=descriptor}
var lastWithValue=descriptors.length-1;for(var i=0;i<this.fields.length;i++){var field=this.fields[i];if(i>lastWithValue){descriptors[i]={value:"",begin:-1}} else {if(field.literal){var descriptor=descriptors[i];if(descriptor.begin<0){descriptor.value="";continue}
var previousField=null;var previousValueComplete=false;for(var j=i-1;j>=0;j--){var current=this.fields[j];if(current.input){previousField=current;previousValueComplete=current.isComplete(descriptors[j].value);if(previousValueComplete){break} else {previousField=null}}}
var nextField=null;var nextValueExists=null;for(var j=i+1;j<this.fields.length&&j<descriptors.length;j++){var current=this.fields[j];if(current.input){nextField=current;nextValueExists=!isEmpty(descriptors[j].value);if(nextValueExists){break} else {nextField=null}}}
if((previousValueComplete&&nextValueExists)||(previousField==null&&nextValueExists)||(nextField==null&&previousValueComplete)){descriptor.value=field.text} else {descriptor.value="";descriptor.begin=-1}}}}
return descriptors}
this.getFieldIndexUnderCaret=function(){var value=this.control.value;var caret=getCaret(this.control);if(caret==null) caret=value.length;var lastPosition=0;var lastInputIndex=null;var lastInputAcceptsMoreText=false;var lastWasLiteral=false;var returnNextInput=isEmpty(value)||caret==0;for(var i=0;i<this.fields.length;i++){var field=this.fields[i];if(field.input){if(returnNextInput||lastPosition>value.length){return i}
var upTo=field.upTo(value,lastPosition)
if(upTo<0){return i}
if(field.max<0){var nextField=null;if(i<this.fields.length-1){nextField=this.fields[i+1]}
if(caret-1<=upTo&&(nextField==null||nextField.literal)){return i}}
var fieldValue=value.substring(lastPosition,upTo+1);var acceptsMoreText=field.acceptsMoreText(fieldValue);var positionToCheck=acceptsMoreText?caret-1:caret
if(caret>=lastPosition&&positionToCheck<=upTo){return i}
lastInputAcceptsMoreText=acceptsMoreText;lastPosition=upTo+1;lastInputIndex=i} else {if(caret==lastPosition){returnNextInput=!lastInputAcceptsMoreText}
lastPosition+=field.text.length}
lastWasLiteral=field.literal}
return this.fields.length-1}
this.isOnlyFilter=function(){if(this.fields==null||this.fields.length==0){return true}
if(this.fields.length>1){return false}
var field=this.fields[0];return field.input&&field.min<=1&&field.max<=0}
this.transformsCase=function(){if(this.fields==null||this.fields.length==0){return false}
for(var i=0;i<this.fields.length;i++){var field=this.fields[i];if(field.upper||field.lower||field.capitalize){return true}}
return false}}
function NumberMask(parser,control,maxIntegerDigits,allowNegative,keyPressFunction,keyDownFunction,keyUpFunction,blurFunction,updateFunction,leftToRight,changeFunction){if(!isInstance(parser,NumberParser)){alert("Illegal NumberParser instance");return}
this.parser=parser;control=validateControlToMask(control);if(!control){alert("Invalid control to mask");return} else {this.control=control;prepareForCaret(this.control);this.control.supportsCaret=isCaretSupported(this.control)}
this.maxIntegerDigits=maxIntegerDigits||-1;this.allowNegative=allowNegative||JST_DEFAULT_ALLOW_NEGATIVE;this.leftToRight=leftToRight||JST_DEFAULT_LEFT_TO_RIGHT;this.control.mask=this;this.control.ignore=false;this.control.swapSign=false;this.control.toDecimal=false;this.control.oldValue=this.control.value;this.keyDownFunction=keyDownFunction||null;this.keyPressFunction=keyPressFunction||null;this.keyUpFunction=keyUpFunction||null;this.blurFunction=blurFunction||null;this.updateFunction=updateFunction||null;this.changeFunction=changeFunction||null;function onKeyDown(event){if(window.event){event=window.event}
var keyCode=typedCode(event);this.ignore=event.altKey||event.ctrlKey||inArray(keyCode,JST_IGNORED_KEY_CODES);if(this.mask.keyDownFunction!=null){var ret=invokeAsMethod(this,this.mask.keyDownFunction,[event,this.mask]);if(ret==false){return preventDefault(event)}}
return true}
observeEvent(this.control,"keydown",onKeyDown);function onKeyPress(event){if(window.event){event=window.event}
var keyCode=typedCode(event);var typedChar=String.fromCharCode(keyCode);if(this.mask.keyPressFunction!=null){var ret=invokeAsMethod(this,this.mask.keyPressFunction,[event,this.mask]);if(ret==false){return preventDefault(event)}}
if(this.ignore){return true}
this.oldValue=this.value;if(typedChar=='-'){if(this.mask.allowNegative){this.swapSign=true;applyNumberMask(this.mask,false,false)}
return preventDefault(event)}
if(this.mask.leftToRight&&typedChar==this.mask.parser.decimalSeparator&&this.mask.parser.decimalDigits!=0){this.toDecimal=true;if(this.supportsCaret){return preventDefault(event)}}
this.swapSign=false;this.toDecimal=false;this.accepted=false;if(this.mask.leftToRight&&typedChar==this.mask.parser.decimalSeparator){if(this.mask.parser.decimalDigits==0||this.value.indexOf(this.mask.parser.decimalSeparator)>=0){this.accepted=true;return preventDefault(event)} else {return true}}
this.accepted=onlyNumbers(typedChar);if(!this.accepted){return preventDefault(event)}}
observeEvent(this.control,"keypress",onKeyPress);function onKeyUp(event){if(this.mask.parser.decimalDigits<0&&!this.mask.leftToRight){alert("A NumberParser with unlimited decimal digits is not supported on NumberMask when the leftToRight property is false");this.value="";return false}
if(window.event){event=window.event}
var keyCode=typedCode(event);var isBackSpace=(keyCode==8)&&JST_NUMBER_MASK_APPLY_ON_BACKSPACE;if(this.supportsCaret&&(this.toDecimal||(!this.ignore&&this.accepted)||isBackSpace)){if(isBackSpace&&this.mask.getAsNumber()==0){this.value=""}
applyNumberMask(this.mask,false,isBackSpace)}
if(this.mask.keyUpFunction!=null){var ret=invokeAsMethod(this,this.mask.keyUpFunction,[event,this.mask]);if(ret==false){return preventDefault(event)}}
return true}
observeEvent(this.control,"keyup",onKeyUp);function onFocus(event){if(this.mask.changeFunction!=null){this._lastValue=this.value}}
observeEvent(this.control,"focus",onFocus);function onBlur(event){if(window.event){event=window.event}
if(JST_MASK_VALIDATE_ON_BLUR){applyNumberMask(this.mask,true,false)}
if(this.mask.changeFunction!=null){if(this._lastValue!=this.value&&this.mask.changeFunction!=null){var e={};for(property in event){e[property]=event[property]}
e.type="change";invokeAsMethod(this,this.mask.changeFunction,[e,this.mask])}}
if(this.mask.blurFunction!=null){var ret=invokeAsMethod(this,this.mask.blurFunction,[event,this.mask]);if(ret==false){return preventDefault(event)}}
return true}
observeEvent(this.control,"blur",onBlur);this.isComplete=function(){return this.control.value!=""}
this.getAsNumber=function(){var number=this.parser.parse(this.control.value);if(isNaN(number)){number=null}
return number}
this.setAsNumber=function(number){var value="";if(isInstance(number,Number)){value=this.parser.format(number)}
this.control.value=value;this.update()}
this.update=function(){applyNumberMask(this,true,false)}}
function DateMask(parser,control,validate,validationMessage,keyPressFunction,keyDownFunction,keyUpFunction,blurFunction,updateFunction,changeFunction){if(isInstance(parser,String)){parser=new DateParser(parser)}
if(!isInstance(parser,DateParser)){alert("Illegal DateParser instance");return}
this.parser=parser;this.extraKeyPressFunction=keyPressFunction||null;function maskKeyPressFunction(event,dateMask){dateMask.showValidation=true;if(dateMask.extraKeyPressFunction!=null){var ret=invokeAsMethod(this,dateMask.extraKeyPressFunction,[event,dateMask]);if(ret==false){return false}}
return true}
this.extraBlurFunction=blurFunction||null;function maskBlurFunction(event,dateMask){var control=dateMask.control;if(dateMask.validate&&control.value.length>0){var controlValue=control.value.toUpperCase();controlValue=controlValue.replace(/A[^M]/,"AM");controlValue=controlValue.replace(/A$/,"AM");controlValue=controlValue.replace(/P[^M]/,"PM");controlValue=controlValue.replace(/P$/,"PM");var date=dateMask.parser.parse(controlValue);if(date==null){var msg=dateMask.validationMessage;if(dateMask.showValidation&&!isEmpty(msg)){dateMask.showValidation=false;msg=replaceAll(msg,"${value}",control.value);msg=replaceAll(msg,"${mask}",dateMask.parser.mask);alert(msg)}
control.value="";control.focus()} else {control.value=dateMask.parser.format(date)}}
if(dateMask.extraBlurFunction!=null){var ret=invokeAsMethod(this,dateMask.extraBlurFunction,[event,dateMask]);if(ret==false){return false}}
return true}
var fields=[];var old='';var mask=this.parser.mask;while(mask.length>0){var field=mask.charAt(0);var size=1;var maxSize=-1;var padFunction=null;while(mask.charAt(size)==field){size++}
mask=mid(mask,size);var accepted=JST_CHARS_NUMBERS;switch(field){case 'd':case 'M':case 'h':case 'H':case 'm':case 's':maxSize=2;break;case 'y':padFunction=JST_DEFAULT_DATE_MASK_YEAR_PAD_FUNCTION;if(size==2){maxSize=2} else {maxSize=4}
break;case 'a':case 'A':case 'p':case 'P':maxSize=2;padFunction=JST_DEFAULT_DATE_MASK_AM_PM_PAD_FUNCTION;accepted='aApP';break;case 'S':maxSize=3;break}
var input;if(maxSize==-1){input=new Literal(field)} else {input=new Input(accepted,size,maxSize);if(padFunction==null){input.padFunction=new Function("text","return lpad(text, "+maxSize+", '0')")} else {input.padFunction=padFunction}}
fields[fields.length]=input}
this.base=InputMask;this.base(fields,control,maskKeyPressFunction,keyDownFunction,keyUpFunction,maskBlurFunction,updateFunction,changeFunction);this.validate=validate==null?JST_DEFAULT_DATE_MASK_VALIDATE:booleanValue(validate);this.showValidation=true;this.validationMessage=validationMessage||JST_DEFAULT_DATE_MASK_VALIDATION_MESSAGE;this.control.dateMask=this;this.getAsDate=function(){return this.parser.parse(this.control.value)}
this.setAsDate=function(date){var value="";if(isInstance(date,Date)){value=this.parser.format(date)}
this.control.value=value;this.update()}}
function SizeLimit(control,maxLength,output,outputText,updateFunction,keyUpFunction,blurFunction,keyDownFunction,keyPressFunction,changeFunction){control=validateControlToMask(control);if(!control){alert("Invalid control to limit size");return} else {this.control=control;prepareForCaret(control)}
if(!isInstance(maxLength,Number)){alert("Invalid maxLength");return}
this.control=control;this.maxLength=maxLength;this.output=output||null;this.outputText=outputText||JST_DEFAULT_LIMIT_OUTPUT_TEXT;this.updateFunction=updateFunction||null;this.keyDownFunction=keyDownFunction||null;this.keyPressFunction=keyPressFunction||null;this.keyUpFunction=keyUpFunction||null;this.blurFunction=blurFunction||null;this.changeFunction=changeFunction||null;this.control.sizeLimit=this;function onKeyDown(event){if(window.event){event=window.event}
var keyCode=typedCode(event);this.ignore=inArray(keyCode,JST_IGNORED_KEY_CODES);if(this.sizeLimit.keyDownFunction!=null){var ret=invokeAsMethod(this,this.sizeLimit.keyDownFunction,[event,this.sizeLimit]);if(ret==false){return preventDefault(event)}}}
observeEvent(this.control,"keydown",onKeyDown);function onKeyPress(event){if(window.event){event=window.event}
var keyCode=typedCode(event);var typedChar=String.fromCharCode(keyCode);var allowed=this.ignore||this.value.length<this.sizeLimit.maxLength;if(this.sizeLimit.keyPressFunction!=null){var ret=invokeAsMethod(this,this.sizeLimit.keyPressFunction,[event,this.sizeLimit]);if(ret==false){return preventDefault(event)}}
if(!allowed){preventDefault(event)}}
observeEvent(this.control,"keypress",onKeyPress);function onKeyUp(event){if(window.event){event=window.event}
if(this.sizeLimit.keyUpFunction!=null){var ret=invokeAsMethod(this,this.sizeLimit.keyUpFunction,[event,this.sizeLimit]);if(ret==false){return false}}
return checkSizeLimit(this,false)}
observeEvent(this.control,"keyup",onKeyUp);function onFocus(event){if(this.mask&&this.mask.changeFunction!=null){this._lastValue=this.value}}
observeEvent(this.control,"focus",onFocus);function onBlur(event){if(window.event){event=window.event}
var ret=checkSizeLimit(this,true);if(this.mask&&this.mask.changeFunction!=null){if(this._lastValue!=this.value&&this.sizeLimit.changeFunction!=null){var e={};for(property in event){e[property]=event[property]}
e.type="change";invokeAsMethod(this,this.sizeLimit.changeFunction,[e,this.sizeLimit])}}
if(this.sizeLimit.blurFunction!=null){var ret=invokeAsMethod(this,this.sizeLimit.blurFunction,[event,this.sizeLimit]);if(ret==false){return false}}
return ret}
observeEvent(this.control,"blur",onBlur);this.update=function(){checkSizeLimit(this.control,true)}
this.update()}
function validateControlToMask(control){control=getObject(control)
if(control==null){return false} else if(!(control.type)||(!inArray(control.type,["text","textarea","password"]))){return false} else {if(/MSIE/.test(navigator.userAgent)&&!window.opera){observeEvent(self,"unload",function(){control.mask=control.dateMask=control.sizeLimit=null})}
return control}}
function applyMask(mask,isBlur){var fields=mask.fields;if((fields==null)||(fields.length==0)){return}
var control=mask.control;if(isEmpty(control.value)&&isBlur){return true}
var value=control.value;var typedChar=control.typedChar;var typedIndex=control.caretPosition;var setFixedLiteral=control.setFixedLiteral;var currentFieldIndex=mask.getFieldIndexUnderCaret();var fieldDescriptors=mask.getCurrentFields();var currentDescriptor=fieldDescriptors[currentFieldIndex];if(isBlur||!isEmpty(typedChar)){var out=new StringBuffer(fields.length);var caretIncrement=1;for(var i=0;i<fields.length;i++){var field=fields[i];var descriptor=fieldDescriptors[i];var padValue=(setFixedLiteral==i+1);if(currentFieldIndex==i){if(!isEmpty(typedIndex)&&!isEmpty(typedChar)&&field.isAccepted(typedChar)){var fieldStartsAt=descriptor.begin<0?value.length:descriptor.begin;var insertAt=Math.max(0,typedIndex-fieldStartsAt);if(field.input&&field.acceptsMoreText(descriptor.value)){descriptor.value=insertString(descriptor.value,insertAt,typedChar)} else {var prefix=left(descriptor.value,insertAt);var suffix=mid(descriptor.value,insertAt+1);descriptor.value=prefix+typedChar+suffix}}} else if(currentFieldIndex==i+1&&field.literal&&typedIndex==descriptor.begin){caretIncrement+=field.text.length}
if(isBlur&&!isEmpty(descriptor.value)&&i==fields.length-1&&field.input){padValue=true
}
if(padValue){var oldValue=descriptor.value;descriptor.value=field.pad(descriptor.value);var inc=descriptor.value.length-oldValue.length;if(inc>0){caretIncrement+=inc}}
out.append(descriptor.value)}
value=out.toString()}
fieldDescriptors=mask.getCurrentFields(value);var out=new StringBuffer(fields.length);for(var i=0;i<fields.length;i++){var field=fields[i];var descriptor=fieldDescriptors[i];if(field.literal&&(setFixedLiteral==i||(i<fields.length-1&&!isEmpty(fieldDescriptors[i+1].value)))){descriptor.value=field.text}
out.append(descriptor.value)}
control.value=out.toString();if(control.caretPosition!=null&&!isBlur){if(control.pad){setCaretToEnd(control)} else {setCaret(control,typedIndex+control.value.length-value.length+caretIncrement)}}
if(mask.updateFunction!=null){mask.updateFunction(mask)}
control.typedChar=null;control.fieldDescriptors=null;control.currentFieldIndex=null;return false}
function nonDigitsToCaret(value,caret){if(caret==null){return null}
var nonDigits=0;for(var i=0;i<caret&&i<value.length;i++){if(!onlyNumbers(value.charAt(i))){nonDigits++}}
return nonDigits}
function applyNumberMask(numberMask,isBlur,isBackSpace){var control=numberMask.control;var value=control.value;if(value==""){return true}
var parser=numberMask.parser;var maxIntegerDigits=numberMask.maxIntegerDigits;var swapSign=false;var toDecimal=false;var leftToRight=numberMask.leftToRight;if(control.swapSign==true){swapSign=true;control.swapSign=false}
if(control.toDecimal==true){toDecimal=value.indexOf(parser.decimalSeparator)<0;control.toDecimal=false}
var intPart="";var decPart="";var isNegative=value.indexOf('-')>=0||value.indexOf('(')>=0;if(value==""){value=parser.format(0)}
value=replaceAll(value,parser.groupSeparator,'')
value=replaceAll(value,parser.currencySymbol,'')
value=replaceAll(value,'-','')
value=replaceAll(value,'(','')
value=replaceAll(value,')','')
value=replaceAll(value,' ','')
var pos=value.indexOf(parser.decimalSeparator);var hasDecimal=(pos>=0);var caretAdjust=0;if(leftToRight){if(hasDecimal){intPart=value.substr(0,pos);decPart=value.substr(pos+1)} else {intPart=value}
if(isBlur&&parser.decimalDigits>0){decPart=rpad(decPart,parser.decimalDigits,'0')}} else {var decimalDigits=parser.decimalDigits;value=replaceAll(value,parser.decimalSeparator,'');intPart=left(value,value.length-decimalDigits);decPart=lpad(right(value,decimalDigits),decimalDigits,'0')}
var zero=onlySpecified(intPart+decPart,'0');if((!isEmpty(intPart)&&!onlyNumbers(intPart))||(!isEmpty(decPart)&&!onlyNumbers(decPart))){control.value=control.oldValue;return true}
if(leftToRight&&parser.decimalDigits>=0&&decPart.length>parser.decimalDigits){decPart=decPart.substring(0,parser.decimalDigits)}
if(maxIntegerDigits>=0&&intPart.length>maxIntegerDigits){caretAdjust=maxIntegerDigits-intPart.length-1;intPart=left(intPart,maxIntegerDigits)}
if(zero){isNegative=false} else if(swapSign){isNegative=!isNegative}
if(!isEmpty(intPart)){while(intPart.charAt(0)=='0'){intPart=intPart.substr(1)}}
if(isEmpty(intPart)){intPart="0"}
if((parser.useGrouping)&&(!isEmpty(parser.groupSeparator))){var group,temp="";for(var i=intPart.length;i>0;i-=parser.groupSize){group=intPart.substring(intPart.length-parser.groupSize);intPart=intPart.substring(0,intPart.length-parser.groupSize);temp=group+parser.groupSeparator+temp}
intPart=temp.substring(0,temp.length-1)}
var out=new StringBuffer();var oneFormatted=parser.format(isNegative?-1:1);var appendEnd=true;pos=oneFormatted.indexOf('1');out.append(oneFormatted.substring(0,pos));out.append(intPart);if(leftToRight){if(toDecimal||!isEmpty(decPart)){out.append(parser.decimalSeparator).append(decPart);appendEnd=!toDecimal}} else {if(parser.decimalDigits>0){out.append(parser.decimalSeparator)}
out.append(decPart)}
if(appendEnd&&oneFormatted.indexOf(")")>=0){out.append(")")}
var caret=getCaret(control);var oldNonDigitsToCaret=nonDigitsToCaret(control.value,caret),hadSymbol;var caretToEnd=toDecimal||caret==null||caret==control.value.length;if(caret!=null&&!isBlur){hadSymbol=control.value.indexOf(parser.currencySymbol)>=0||control.value.indexOf(parser.decimalSeparator)>=0}
control.value=out.toString();if(caret!=null&&!isBlur){if(!hadSymbol&&((value.indexOf(parser.currencySymbol)>=0)||(value.indexOf(parser.decimalSeparator)>=0))){caretToEnd=true}
if(!caretToEnd){var newNonDigitsToCaret=nonDigitsToCaret(control.value,caret);if(isBackSpace){caretAdjust=0}
setCaret(control,caret+caretAdjust+newNonDigitsToCaret-oldNonDigitsToCaret)} else {setCaretToEnd(control)}}
if(numberMask.updateFunction!=null){numberMask.updateFunction(numberMask)}
return false}
function checkSizeLimit(control,isBlur){var sizeLimit=control.sizeLimit;var max=sizeLimit.maxLength;var diff=max-control.value.length;if(control.value.length>max){control.value=left(control.value,max);setCaretToEnd(control)}
var size=control.value.length;var charsLeft=max-size;if(sizeLimit.output!=null){var text=sizeLimit.outputText;text=replaceAll(text,"${size}",size);text=replaceAll(text,"${left}",charsLeft);text=replaceAll(text,"${max}",max);setValue(sizeLimit.output,text)}
if(isInstance(sizeLimit.updateFunction,Function)){sizeLimit.updateFunction(control,size,max,charsLeft)}
return true}
function MaskField(){this.literal=false;this.input=false;this.upTo=function(text,fromIndex){return-1}}
function Literal(text){this.base=MaskField;this.base();this.text=text;this.literal=true;this.isAccepted=function(chr){return onlySpecified(chr,this.text)}
this.upTo=function(text,fromIndex){return text.indexOf(this.text,fromIndex)}}
function Input(accepted,min,max,padFunction,optional){this.base=MaskField;this.base();this.accepted=accepted;if(min!=null&&max==null){max=min}
this.min=min||1;this.max=max||-1;this.padFunction=padFunction||null;this.input=true;this.upper=false;this.lower=false;this.capitalize=false;this.optional=booleanValue(optional);if(this.min<1){this.min=1}
if(this.max==0){this.max=-1}
if((this.max<this.min)&&(this.max>=0)){this.max=this.min}
this.upTo=function(text,fromIndex){text=text||"";fromIndex=fromIndex||0;if(text.length<fromIndex){return-1}
var toIndex=-1;for(var i=fromIndex;i<text.length;i++){if(this.isAccepted(text.substring(fromIndex,i+1))){toIndex=i} else {break}}
return toIndex}
this.acceptsMoreText=function(text){if(this.max<0) return true;return(text||"").length<this.max}
this.isAccepted=function(text){return((this.accepted==null)||onlySpecified(text,this.accepted))&&((text.length<=this.max)||(this.max<0))}
this.checkLength=function(text){return(text.length>=this.min)&&((this.max<0)||(text.length<=this.max))}
this.isComplete=function(text){text=String(text);if(isEmpty(text)){return this.optional}
return text.length>=this.min}
this.transformValue=function(text){text=String(text);if(this.upper){return text.toUpperCase()} else if(this.lower){return text.toLowerCase()} else if(this.capitalize){return capitalize(text)} else {return text}}
this.pad=function(text){text=String(text);if((text.length<this.min)&&((this.max>=0)||(text.length<=this.max))||this.max<0){var value;if(this.padFunction!=null){value=this.padFunction(text,this.min,this.max)} else {value=text}
if(value.length<this.min){var padChar=' ';if(this.accepted==null||this.accepted.indexOf(' ')>0){padChar=' '} else if(this.accepted.indexOf('0')>0){padChar='0'} else {padChar=this.accepted.charAt(0)}
return left(lpad(value,this.min,padChar),this.min)} else {return value}} else {return text}}}
function Lower(accepted,min,max,padFunction,optional){this.base=Input;this.base(accepted,min,max,padFunction,optional);this.lower=true}
function Upper(accepted,min,max,padFunction,optional){this.base=Input;this.base(accepted,min,max,padFunction,optional);this.upper=true}
function Capitalize(accepted,min,max,padFunction,optional){this.base=Input;this.base(accepted,min,max,padFunction,optional);this.capitalize=true}
function FieldBuilder(){
this.literal=function(text){return new Literal(text)}
this.input=function(accepted,min,max,padFunction,optional){return new Input(accepted,min,max,padFunction,optional)}
this.upper=function(accepted,min,max,padFunction,optional){return new Upper(accepted,min,max,padFunction,optional)}
this.lower=function(accepted,min,max,padFunction,optional){return new Lower(accepted,min,max,padFunction,optional)}
this.capitalize=function(accepted,min,max,padFunction,optional){return new Capitalize(accepted,min,max,padFunction,optional)}
this.inputAll=function(min,max,padFunction,optional){return this.input(null,min,max,padFunction,optional)}
this.upperAll=function(min,max,padFunction,optional){return this.upper(null,min,max,padFunction,optional)}
this.lowerAll=function(min,max,padFunction,optional){return this.lower(null,min,max,padFunction,optional)}
this.capitalizeAll=function(min,max,padFunction,optional){return this.capitalize(null,min,max,padFunction,optional)}
this.inputNumbers=function(min,max,padFunction,optional){return this.input(JST_CHARS_NUMBERS,min,max,padFunction,optional)}
this.inputLetters=function(min,max,padFunction,optional){return this.input(JST_CHARS_LETTERS,min,max,padFunction,optional)}
this.upperLetters=function(min,max,padFunction,optional){return this.upper(JST_CHARS_LETTERS,min,max,padFunction,optional)}
this.lowerLetters=function(min,max,padFunction,optional){return this.lower(JST_CHARS_LETTERS,min,max,padFunction,optional)}
this.capitalizeLetters=function(min,max,padFunction,optional){return this.capitalize(JST_CHARS_LETTERS,min,max,padFunction,optional)}}
var fieldBuilder=new FieldBuilder();
function MaskBuilder(){
this.parse=function(string){if(string==null||!isInstance(string,String)){return this.any()}
var fields=[];var start=null;var lastType=null;var switchField=function(type,text){switch(type){case '_':return fieldBuilder.inputAll(text.length);case '#':return fieldBuilder.inputNumbers(text.length);case 'a':return fieldBuilder.inputLetters(text.length);case 'l':return fieldBuilder.lowerLetters(text.length);case 'u':return fieldBuilder.upperLetters(text.length);case 'c':return fieldBuilder.capitalizeLetters(text.length);default:return fieldBuilder.literal(text)}}
for(var i=0;i<string.length;i++){var c=string.charAt(i);if(start==null){start=i}
var type;var literal=false;if(c=='\\'){if(i==string.length-1){break}
string=left(string,i)+mid(string,i+1);c=string.charAt(i);literal=true}
if(literal){type='?'} else {switch(c){case '?':case '_':type='_';break;case '#':case '0':case '9':type='#';break;case 'a':case 'A':type='a';break;case 'l':case 'L':type='l';break;case 'u':case 'U':type='u';break;case 'c':case 'C':type='c';break;default:type='?'}}
if(lastType!=type&&lastType!=null){var text=string.substring(start,i);fields[fields.length]=switchField(lastType,text);start=i;lastType=type} else {lastType=type
}}
if(start<string.length){var text=string.substring(start);fields[fields.length]=switchField(lastType,text)}
return fields}
this.accept=function(accepted,max){return [fieldBuilder.input(accepted,max)]}
this.any=function(max){return [fieldBuilder.any(max)]}
this.numbers=function(max){return [fieldBuilder.inputNumbers(max)]}
this.decimal=function(){var decimalField=fieldBuilder.inputNumbers();decimalField.optional=true;return [fieldBuilder.inputNumbers(),JST_FIELD_DECIMAL_SEPARATOR,decimalField]}
this.letters=function(max){return [fieldBuilder.inputLetters(max)]}
this.upperLetters=function(max){return [fieldBuilder.upperLetters(max)]}
this.lowerLetters=function(max){return [fieldBuilder.lowerLetters(max)]}
this.capitalizeLetters=function(max){return [fieldBuilder.capitalizeLetters(max)]}}
var maskBuilder=new MaskBuilder();