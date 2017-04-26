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
