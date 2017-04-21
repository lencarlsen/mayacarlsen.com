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
			error: function( jqXhr, textStatus, errorThrown ) {
			    console.log("Post Failed: " + actionUrl + ", error: "+status);
                pleaseWaitDialog.modal('hide');
			 },
			beforeSend: function () {
				pleaseWaitDialog.modal('show');
			},
			complete: function () {
                refreshTable.ajax.reload();
                pleaseWaitDialog.modal('hide');
			}
        });
	});
}
