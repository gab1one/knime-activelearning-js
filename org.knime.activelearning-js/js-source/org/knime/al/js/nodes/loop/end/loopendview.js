

org_knime_al_js_loopendview = function() {
	
	view{};
	var _representation = null;
	var _value = null;
	var _keyedDataset = null;
	var _port = null;
	var _host = null; 
	
	var minWidth = 400;
	var minHeight = 300;
	var defaultFont = "sans-serif";
	var defaultFontSize = 12;
	
	
	view.name = "Active Learn View";

	view.init = function(representation, value) {
		// check if data is avaiable
		if ((!representation.keyedDataset) || representation.keyedDataset.rows.length < 1) {
			d3.select("body").text("Error: No data available");
			return;
		}
		
		_representation = representation;
		_value = value;
		_port = _representation.port;
		
		var body = document.getElementsByTagName("body")[0];
		var width = representation.maxWidth;
		var height = representation.maxHeight;
		var div = document.createElement("div");
		div.setAttribute("class", "quickformcontainer");
		body.appendChild(div);
		if (representation.label) {
			var label = document.createElement("div");
			label.setAttribute("class", "label");
			label.appendChild(document.createTextNode(representation.label));
			div.appendChild(label);
		}
		if (representation.description) {
			div.setAttribute("title", representation.description); 
		}
		
		var element = null;
		if (representation.format == "PNG") {
			var img = document.createElement("img");
			img.setAttribute("src", _representation.host + ":" + 
					_representation.port + "/" );
			div.appendChild(img);
			if (width >= 0) {
				img.style.maxWidth = width + "px";
			}
			if (height >= 0) {
				img.style.maxHeight = height + "px";
			}
		} else {
			var errorText = "Image format not supported: " + representation.imageFormat;
			div.appendChild(document.createTextNode(errorText));
		}
		
		resizeParent();
	};
	

	// createViewbox = function(element, width, height, oldWidth, oldHeight) {
	// element.setAttribute("viewBox", "0 0 " + oldWidth + " " + oldHeight);
	// element.setAttribute("preserveAspectRatio", "xMinYMin meet");
	// element.setAttribute("width", Math.round(width));
	// element.setAttribute("height", Math.round(height));
	// element.style.width = Math.round(width) + "px";
	// element.style.height = Math.round(height) + "px";
	//	}
	
	imageOutput.validate = function() {
		return true;
	};
	
	imageOutput.setValidationErrorMessage = function(message) {
		//TODO display message
	};

	imageOutput.value = function() {
		return null;
	};
	
	return imageOutput;
	
}();
