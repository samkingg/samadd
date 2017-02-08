//document.querySelectorAll('[name="dfwidgetshorttextfield_1148578"]')[0].value= 'adreddds';


function copyToClipboard(text) {

    if (window.clipboardData && window.clipboardData.setData) {
        // IE specific code path to prevent textarea being shown while dialog is visible.
        return clipboardData.setData("Text", text); 

    } else if (document.queryCommandSupported && document.queryCommandSupported("copy")) {
        var textarea = document.createElement("textarea");
        textarea.textContent = text;
        textarea.style.position = "fixed";  // Prevent scrolling to bottom of page in MS Edge.
        document.body.appendChild(textarea);
        textarea.select();
        try {
            return document.execCommand("copy");  // Security exception may be thrown by some browsers.
        } catch (ex) {
            console.warn("Copy to clipboard failed.", ex);
            return false;
        } finally {
            document.body.removeChild(textarea);
        }
    }
}
	//var smid = document.getElementById('complianceSelectTwo').value
	//copyToClipboard(document.getElementById('complianceSelectTwo').value);


//toastr.info(messages.generating_link);
var fam = document.getElementById('complianceSelectTwo').value;
var adres1 = document.querySelectorAll('[name="addressLine3"]')[0].value;
var dateex = document.getElementById('primaryExpiryDate').value;
var dateiss = document.getElementById('primaryIssueDate').value;
var birthc = document.querySelectorAll('[name="complianceCountryOfBirth"]')[0].value;
var birthd = document.getElementById('receiverDateOfBirth').value;
var cardnum = document.getElementById('primaryInfoOne').value;
var cintiss = document.getElementById('primaryIdCountryOfIssue').value;
var cardtyp = document.querySelectorAll('[name="primaryId"]')[0].value;
//var phone = document.querySelectorAll('[name="addressLine1"]')[0].value;
var phone = document.getElementById('phoneNumber').value;
// document.querySelectorAll('[name="mobileNumber"]')[0].value;
var namerec = document.getElementById("receiverNameProxy").innerText;
var part1 =('#6#')+(birthd) + ('#6#')+('#7#')+(cardnum) + ('#7#')+('#8#')+(cintiss) + ('#8#')+('#9#')+(cardtyp)+ ('#9#')+('#10#')+(phone)+('#10#')+('#11#')+(namerec)+('#11#');
var allkl =(('readsamcards')+(fam) + ('#1#')+('#2#')+(adres1) + ('#2#')+('#3#')+(dateex) + ('#3#')+('#4#')+(dateiss) + ('#4#')+('#5#')+(birthc) + ('#5#') + (part1));


 var result = copyToClipboard(allkl);
 
 
 //var result = copyToClipboard(smid);
console.log("copied?", result);

