//Global Variable
var errorArr  = [];
function validateAndSubmit() {
    errorArr=[];
    var isCVNValid =0;
    var isCreditCardValid = validateCreditcard();
    var isExpMonthValid=validateExpMonth();
    var isCardTypeValid=0;
    var isSelectCardTypeValid=0;
    if (isCreditCardValid == 1)
    {
        $('card_number').val($('#ccnumber').val().replace(/[^\d]/g, ''));
        isCardTypeValid=setCreditCardType();
        isSelectCardTypeValid = isSelectedCardTypeValid(isCardTypeValid);
        isCVNValid = validateCvv();
        if(isCVNValid==1){
            var cvnNumber = $('#cvv').val().replace(/[^\d]/g, '')
            $('#card_cvn').val(cvnNumber);
        }
    }


    if(isCreditCardValid ==1 && isCVNValid==1 && isExpMonthValid==1 && isCardTypeValid==1 && isSelectCardTypeValid==1){
        var expMonth = $('#expMonth').val();
        var expYear = $('#expYear').val();
        $('#card_expiry_date').val(expMonth+'-'+expYear);
        $('#errorDiv').css('display','none');
        $('#inner-container-error').css('display','none');
        errorArr =[];
    }else{
        $('#errorDiv').html('');
        var listStr = '';
            $(errorArr).each(function (index, value) {
                listStr = listStr + value;
        });
        $('#errorDiv').html("<ul>" + listStr + "</ul>");
        
        
       // $('#errorDiv').html("<ul>"+errorArr+"</ul>");
        $('#errorDiv').css('display','block');
        $('#inner-container-error').css('display','block');
        return false;
    }

}

/**
 * This method will get the card_type
 * visa:001
 * MasterCard: 002
 * Amex: 003
 * Discover:004
 * Credit card Bin range Info - https://en.wikipedia.org/wiki/Bank_card_number
 * @param accountNumber
 * @returns {string}
 */
function setCreditCardType()
{
    var isCardTypeValid=1;
    var cardNumber = $("#ccnumber").val();
    //start without knowing the credit card type
    var result = "unknown";
    //first check for Visa
    if (/^4\d{12}(\d{3})?$/.test(cardNumber))
    {
        result = "001";
    }
    //then check for MasterCard
    else if (/^5\d{15}|36\d{14}$/.test(cardNumber))
    {
        result = "002";
    }
    //then check for AmEx
    else if (/^3[47]\d{13}$/.test(cardNumber))
    {
        result = "003";
    }
    //Check for Discover
    else if(/^6011\d{12}|650\d{13}$/.test(cardNumber)){
        result="004";
    }
    if(result=='unknown'){
        errorArr.push('<li>'+invalidCardNum +'</li>');
        isCardTypeValid=0;
    }
    $("#card_type").val(result);
    return isCardTypeValid;
}

/**
 * This method checks if the card selected in the dropdown matches the card type using the card number.
 * @param isCardTypeValid
 */
function isSelectedCardTypeValid(isCardTypeValid){
    if(isCardTypeValid == 1) {
        var selectedOption = $('#cardOption').val();
        var cardType = $("#card_type").val();
        if (selectedOption == cardType) {
            return 1;
        }
    }
    errorArr.push('<li>'+mismatch+'</li>');
    return 0;
}

//This method will populate the expiration month and year in the drop down
function populateCCExpirationFields(){
    errorArr=[];
    var month = document.getElementById("expMonth");
    var year = document.getElementById("expYear");
    var currentYear = new Date().getFullYear();
    //Empty Month and year
    emptyDropDown(month);
    emptyDropDown(year);

    //populate month and year
    populateDropDown(1, 13, month);
    populateDropDown(currentYear, (currentYear + 21), year);
}

//This method will check that cc takes only digits and is of right format using Luhn's algorithm
function validateCreditcard(){
    var isCreditCardValid=0;
    var input = $('#ccnumber').val();
    var cc_number = input.replace(/[^\d]/g, ''); //Saves number only and strips out non numbers.
    var isCCNumberValid = checkCCUsingLuhnAlgorithm(cc_number);
    if(isCCNumberValid){
        isCreditCardValid=1;
        $('#card_number').val(cc_number);
    }else{
        errorArr.push('<li>'+invalidCardNum+'</li>');
    }
    return isCreditCardValid;
}

//This method will check that cvv takes only digits and is of right format
function validateCvv(){

    var input = $('#cvv').val();
    var cvv_number = input.replace(/[^\d]/g, ''); //Saves number only and strips out non numbers.
    var cardNumber = $('#card_number').val();
    var isCvvValidFlag = isCvvValid(cvv_number,$('#card_type').val());
    if(isCvvValidFlag!=1){
        errorArr.push('<li>'+invalidCvn+'</li>');
    }
    return isCvvValidFlag;
}

//This method uses Luhn's algorithm to test a valid credit card.
//https://en.wikipedia.org/wiki/Luhn_algorithm
function checkCCUsingLuhnAlgorithm(input)
{
    var sum = 0;
    var numdigits = input.length;
    var parity = numdigits % 2;
    for(var i=0; i < numdigits; i++) {
        var digit = parseInt(input.charAt(i))
        if(i % 2 == parity) digit *= 2;
        if(digit > 9) digit -= 9;
        sum += digit;
    }
    return (sum % 10) == 0;
}

/***
 * Validates if CVV is of correct length
 * CVV Length=3 for MC, Discover and Visa
 * CVV Length=4 for Amex
 * @param cvv
 * @param cardType
 */
function isCvvValid(cvv,cardType){
    var isCvvValid=0;
    if(cardType!="003"){
        if(cvv.length==3){
            isCvvValid=1;
        }
    }else if(cardType=="003" && cvv.length==4){
        isCvvValid=1;
    }
    return isCvvValid;
}

//This method will populate with drop downlist from begin value to end value
function populateDropDown(beginVal,endVal,object) {
    for (var optValue = beginVal; optValue<endVal; optValue++){
        var option = document.createElement("option");
        option.text = optValue;
        if(optValue<10){
            optValue="0"+optValue;
        }
        option.value = optValue;
        object.add(option);
    }
}

//This method will empty the drop downList
function emptyDropDown(selectObj){
    if(selectObj.length > 0){
        for(var i=(selectObj.length -1);i>=0;i--){
            selectObj.remove(i);
        }
    }
}

//This method will make sure that you do not choose month earlier than current month in the current year
function validateExpMonth(){
    var isMonthValid=1;
    var currentYear = new Date().getFullYear();
    if(currentYear == $('#expYear').val()){
        var currentMonth = (new Date().getMonth()) + 1;
        if(currentMonth > $('#expMonth').val()){
            isMonthValid=0;
        }
    }

    if(isMonthValid==0){
        errorArr.push("<li>"+invaliddate+"</li>");
    }
    return isMonthValid;
}