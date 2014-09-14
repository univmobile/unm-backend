<%@ page pageEncoding="UTF-8"%> 

<script type="text/javascript">

	function initialize_adminMenu() {
	
		$('#ul-adminMenu-data').menu();
		$('#ul-adminMenu-system').menu();
		
		$('#ul-adminMenu li.top').bind('mouseover', function() {
			$(this).find('ul').css('visibility', 'visible');
		}).bind('mouseout', function() {
			$(this).find('ul').css('visibility', 'hidden');
		});
		
		$('#ul-adminMenu-data-regions').bind('mouseout', function() {
			$(this).css('visibility', 'hidden');
		});
	}
	
	$(document).ready(initialize_adminMenu);

</script>
