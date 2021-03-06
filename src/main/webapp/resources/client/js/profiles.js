let container;
let avatar;
let fullName;
let email;
let address;
let phoneNumber;
let editSelect;
let editAction;
let avatarInput;
let submitButton;
$(document).ready(function() {
    container = $("div#accountInfoContainer");
    avatar = $("div.imgSetVaTi");
    fullName = $("table>tbody>tr>td#fullName");
    email = $("table>tbody>tr>td#email");
    address = $("table>tbody>tr>td#address");
    phoneNumber = $("table>tbody>tr>td#phoneNumber");
    editSelect = $("div#editSelect");
    editAction = $("div#editAction");
    submitButton = $("form button:submit");
    avatarInput = $("input#avatar-upload");
    avatarInput.change(function() {
		$("p#updateAvatarWarning").prepend("<small class='text-primary'>" + avatarInput[0].files[0].name + "</small><br>");
		removeOldAvatar();
	})
	let firstNameInput = fullName.children("div").children("input:first-child");
	firstNameInput.focus(function() {
		clearWarning(firstNameInput);
	})
	let lastNameInput = fullName.children("div").children("input:last-child");
	lastNameInput.focus(function() {
		clearWarning(lastNameInput);
	})
	let addressInput = address.children("input");
	addressInput.focus(function() {
		clearWarning(addressInput);
	})
	let phoneNumberInput = phoneNumber.children("input");
	phoneNumberInput.focus(function() {
		clearWarning(phoneNumberInput);
	})
})
function editInformation() {
    container.slideUp(1000);
    setTimeout(function() {
        hideSomething([
            fullName.children("span"),
            email.children("span"),
            address.children("span"),
            phoneNumber.children("span"),
            editSelect
        ]);
        showSomething([
            avatar.children("div"),
            fullName.children("div"),
            email.children("input"),
            address.children("input"),
            phoneNumber.children("input"),
            editAction
        ]);
        container.fadeIn(2000);
    }, 1000);
}
function canceEdit() {
    container.fadeOut(1000);
    setTimeout(function() {
        hideSomething([
            avatar.children("div"),
            fullName.children("div"),
            email.children("input"),
            address.children("input"),
            phoneNumber.children("input"),
            editAction
        ]);
        showSomething([
            fullName.children("span"),
            email.children("span"),
            address.children("span"),
            phoneNumber.children("span"),
            editSelect
        ]);
        container.slideDown(2000);
    }, 1000);

}
function showSomething(elements) {
    elements.forEach((element) => {
        element.removeClass("d-none");
    })
}
function hideSomething(elements) {
    elements.forEach((element) => {
        element.addClass("d-none");
    })
}
function deleteAvatar() {
    if(confirm("X??c nh???n x??a ???nh ?????i di???n")) {
		$.post(
			"https://love-and-care.herokuapp.com/quan-ly/xoa-anh-dai-dien",
			function(result) {
				if(result == true) {
			        removeOldAvatar();
					alert("???? x??a ???nh!\nH??y th??m ???nh ?????i di???n m???i ????? ai ???? nh???n ra b???n :)");
				} else {
			        alert("X??a ???nh kh??ng th??nh c??ng! H??y th??? l???i sau..");
				}
			}
		);
    }
}
function removeOldAvatar() {
	avatar.children("i").remove();
	avatar.children("img").remove();
    avatar.children("div").children("button").remove();
    avatar.prepend(
		"<i class='bg-light text-success' style='display: inline-block; height: 200px; width: 150px;'>???nh ?????i di???n</i>"
	);
	$("p#updateAvatarWarning").append("H??nh ???nh s??? ???????c l??u l???i sau khi c???p nh???t! C??c th??ng tin ???????c ?????ng b??? t??? l???n ????ng nh???p ti???p theo :v");
}
function verifyUpdateProfile() {
	let ok = (verifyTextLength(fullName.children("div").children("input:first-child"), 50, true) &&
		verifyTextLength(fullName.children("div").children("input:last-child"), 50, true) &&
		verifyTextLength(address.children("input"), 100, false) &&
		verifyTextLength(phoneNumber.children("input"), 15, false) &&
		checkFileSize());
	return ok;
}
function verifyTextLength(inputText, length, required) {
	if(inputText.val().length > length) {
		inputText.addClass("bg-danger text-light");
		editAction.children("span").text("T???i ??a " + length + " k?? t??? cho tr?????ng m??u ?????");
		submitButton.attr("disabled", "disabled");
		return false;
	} else if(inputText.val() == "" && required) {
		inputText.addClass("bg-warning text-light");
		editAction.children("span").text("H??y nh???p ?????y ????? th??ng tin tr?????c khi c???p nh???t.");
		submitButton.attr("disabled", "disabled");
		return false;
	}
	return true;
}
function clearWarning(inputText) {
	inputText.removeClass("bg-danger bg-warning text-light");
	editAction.children("span").text("");
	submitButton.removeAttr("disabled");
}
function checkFileSize() {
	if(avatarInput != null) {
		if(avatarInput[0].files[0].size > 5*1024*1000) {
			editAction.children("span").text("Dung l?????ng ???nh t???i ??a l?? 5Mb");
			return false;
		}
	}
	return true;
}