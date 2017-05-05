Date.prototype.fullMonth = function() {
    if (this.getMonth() == 0){this.getMonthName = "January"};
    if (this.getMonth() == 1){this.getMonthName = "February"};
    if (this.getMonth() == 2){this.getMonthName = "March"};
    if (this.getMonth() == 3){this.getMonthName = "April"};
    if (this.getMonth() == 4){this.getMonthName = "May"};
    if (this.getMonth() == 5){this.getMonthName = "June"};
    if (this.getMonth() == 6){this.getMonthName = "July"};
    if (this.getMonth() == 7){this.getMonthName = "August"};
    if (this.getMonth() == 8){this.getMonthName = "Spetember"};
    if (this.getMonth() == 9){this.getMonthName = "October"};
    if (this.getMonth() == 10){this.getMonthName = "November"};
    if (this.getMonth() == 11){this.getMonthName = "December"};
};

function getFullDateTimeString( dateTime ) {
	  date = new Date( dateTime );
	  date.fullMonth();
	  var year = date.getFullYear();
	  var month = date.getMonthName();
	  var day = date.getDate();
	  var hour = convertIntString( date.getHours() );
	  var minutes = convertIntString( date.getMinutes() );
	  return month + day + ", " + year + " " + hour + ":" + minutes;
}

function getDateTime( dateTime ) {
	  date = new Date( dateTime );  
	  var year = date.getFullYear();
	  var month = convertIntString( date.getMonth()+1 );
	  var day = convertIntString( date.getDate() );
	  var hour = convertIntString( date.getHours() );
	  var minutes = convertIntString( date.getMinutes() );
	  return year + "-" + month + "-" + day + " " + hour + ":" + minutes;
}

function convertIntString( num ) {
	var s = num;  
	if (num < 10) {
		  s = "0" + s;
	}
	return s;
}

// Wait Dialog
var pleaseWaitDialog = 
	$('<div class="modal fade bs-example-modal-sm" id="pleaseWaitDialog" tabindex="-1" role="dialog" aria-hidden="true" data-backdrop="static">'
		+ '  <div class="modal-dialog modal-sm">'
		+ '    <div class="modal-content">'
		+ '      <div class="modal-header">'
		+ '        <h4 class="modal-title">'
		+ '        <span class="glyphicon glyphicon-time"></span> Please Wait</h4>'
		+ '      </div>'
		+ '      <div class="modal-body">'
		+ '        <div class="progress">'
		+ '          <div class="progress-bar progress-bar-info progress-bar-striped active" style="width: 100%"></div>'
		+ '        </div>'
		+ '      </div>'
		+ '    </div>'
		+ '  </div>'
		+ '</div>');

function submitForm( formId, actionUrl, refreshTable ) {
	$(formId).submit(function(e) {
        //prevent Default functionality
        e.preventDefault();

        $.ajax({   
            url: actionUrl,
			type: "POST",
			dataType: 'text',
			contentType: 'application/x-www-form-urlencoded',
			data: $(formId).serialize(),
			success: function( data, textStatus, jQxhr ) {
			    console.log("Post Successful: " + actionUrl);
			},
			error: function( jqXHR, textStatus, errorThrown ) {
			    console.log("Post Failed: " + actionUrl + ", error: "+textStatus + ", "+ jqXHR.status);
                pleaseWaitDialog.modal('hide');
                alert(getHttpError( jqXHR.status ));
			},
			beforeSend: function () {
				pleaseWaitDialog.modal('show');
			},
			complete: function () {
                refreshTable.ajax.reload();
                pleaseWaitDialog.modal('hide');
            	// Unbind so form is not submitted multiple times since every save will create a new form bind
                $(formId).unbind("submit");
			}
        });
	});
}

function getHttpError(httpStatusCode) {
    if (httpStatusCode == 401) {
	    return "Unauthorized Access!";
    } else if (httpStatusCode == 405) {
	    return "Unauthorized Access: Method Not Allowed!";
    } else if (httpStatusCode == 500) {
	    return "Internal Server Error!";
    } else {
	    return "Error occured while processing request. Error Code: "+jqXHR.status;
    }
}

/* ---------------------------------- */
/* ----------  File Upload ---------- */
/* ---------------------------------- */

$(document).on('click', '#close-preview', function(){ 
    $('.image-preview').popover('hide');
    // Hover befor close the preview
    $('.image-preview').hover(
        function () {
           $('.image-preview').popover('show');
        }, 
         function () {
           $('.image-preview').popover('hide');
        }
    );    
});

$(function() {
    // Create the close button
    var closebtn = $('<button/>', {
        type:"button",
        text: 'x',
        id: 'close-preview',
        style: 'font-size: initial;',
    });
    closebtn.attr("class","close pull-right");
    // Set the popover default content
    $('.image-preview').popover({
        trigger:'manual',
        html:true,
        title: "<strong>Preview</strong>"+$(closebtn)[0].outerHTML,
        content: "There's no image",
        placement:'bottom'
    });
    // Clear event
    $('.image-preview-clear').click(function(){
        $('.image-preview').attr("data-content","").popover('hide');
        $('.image-preview-filename').val("");
        $('.image-preview-clear').hide();
        $('.image-preview-input input:file').val("");
        $(".image-preview-input-title").text("Browse"); 
    }); 
    // Create the preview image
    $(".image-preview-input input:file").change(function (){     
        var img = $('<img/>', {
            id: 'dynamic',
            width:250,
            height:200
        });      
        var file = this.files[0];
        var reader = new FileReader();
        // Set preview image into the popover data-content
        reader.onload = function (e) {
            $(".image-preview-input-title").text("Change");
            $(".image-preview-clear").show();
            $(".image-preview-filename").val(file.name);            
            img.attr('src', e.target.result);
            $(".image-preview").attr("data-content",$(img)[0].outerHTML).popover("show");
        }        
        reader.readAsDataURL(file);
    });  
});
