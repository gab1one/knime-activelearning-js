/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2015
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * --------------------------------------------------------------------- *
 *
 */

knime_al_loopend = function() {

	view = {};
	var _representation = null;
	var _value = null;

	var _label_select = null;
	var _rows = null;
	var _selectedRow = 0;
	var _port = "";
	var _host = "";

	view.name = "knime_al_loopend";

	view.init = function(representation, value) {
		// check if data is avaiable
		if ((!representation.rowIDs) || representation.rowIDs.length < 1) {
			d3.select("body").text("Error: No data available");
			return;
		}

		_representation = representation;
		_rows = representation.rowIDs;
		_value = value;
		_port = _representation.serverPort;
		_host = _representation.hostAddress;

		var body = document.getElementsByTagName("body")[0];

		// ImgView
		var imgDiv = document.createElement("div");
		imgDiv.class = "quickformcontainer";
		imgDiv.id = "imgDiv";
		body.appendChild(imgDiv);

		// append initial image
		imgDiv.appendChild(createImg(_rows[0]));
		imgDiv.refreshImage = function() {
			$("#internalImg").remove();
			imgDiv.appendChild(createImg(_rows[_selectedRow]));
		};

		// Class Selection container
		var label_div = document.createElement('div');
		label_div.id = "label_div";
		label_div.class = "quickformcontainer";
		body.appendChild(label_div);

		// class labels select
		_label_select = document.createElement('select');

		// Give the <select> some attributes
		_label_select.name = "label select";
		_label_select.id = "label_select";

		// Add the previously defined class labels as <option>s
		for (var j = 0; j < _value.classLabels.length; j++) {
			var label_opt = document.createElement('option');
			label_opt.value = _value.classLabels[j];
			label_opt.innerHTML = _value.classLabels[j];
			_label_select.appendChild(label_opt);
		}
		label_div.appendChild(_label_select);

		label_div.refreshLabeling = function() {
			var currentLabel = _value.rowLabels[_rows[_selectedRow]];
			var exists = 0 !== $("#label_select option[value=" + currentLabel
					+ "]").length;
			if (exists) {
				$("#label_select").val(currentLabel);
			}
		};

		var labeling_form = document.createElement("form");
		labeling_form.id = "lform";
		label_div.appendChild(labeling_form);

		// class label input
		var label_input = document.createElement("input");
		label_input.type = "text";
		label_input.name = "Class Label";
		labeling_form.appendChild(label_input);

		// add class button
		var add_btn = document.createElement("input");
		add_btn.type = "submit";
		add_btn.value = "Add Class Label";
		labeling_form.appendChild(add_btn);

		labeling_form.onsubmit = function(event) {
			event.preventDefault();
			var nclass = label_input.value;
			label_input.value = "";

			// don't add twice
			var exists = 0 !== $("#label_select option[value=" + nclass + "]").length;
			if (!exists) {
				label_opt = document.createElement("option");
				label_opt.value = nclass;
				label_opt.innerHTML = nclass;
				_label_select.appendChild(label_opt);
			}
			$("#label_select").val(nclass);
		};

		// add forward and backwards buttons

		var control = document.createElement("div");
		var fwd_btn = document.createElement("button");
		fwd_btn.value = "Next Row";
		fwd_btn.innerHTML = "Next Row";
		fwd_btn.id = "fwd_btn";

		var back_btn = document.createElement("button");
		back_btn.value = "Previous Row";
		back_btn.innerHTML = "Previous Row";
		back_btn.id = "back_btn";

		control.appendChild(back_btn);
		control.appendChild(fwd_btn);
		body.appendChild(control);

		// on click funktions
		fwd_btn.onclick = function() {
			// store the current class label
			_value.rowLabels[_rows[_selectedRow]] = $("#label_select").val();
			// wrap around
			if (_selectedRow === _rows.length - 1) {
				_selectedRow = 0;
			} else {
				_selectedRow = _selectedRow + 1;
			}
			// set label
			label_div.refreshLabeling();
			imgDiv.refreshImage();
		};

		// on click functions
		back_btn.onclick = function() {
			_value.rowLabels[_rows[_selectedRow]] = $("#label_select").val();
			// wrap around
			if (_selectedRow === 0) {
				_selectedRow = _rows.length - 1;
			} else {
				_selectedRow = _selectedRow - 1;
			}
			label_div.refreshLabeling();
			imgDiv.refreshImage();
		};

		// Function that creates the img div's
		function createImg(rowID) {
			var format = _representation.format;

			if (format == "PNG") {
				var img = document.createElement("img");
				img.setAttribute("src", _host + ":" + _port + "/" + rowID);
				img.setAttribute("id", "internalImg");

				img.style.maxWidth = 300 + "px";
				img.style.maxHeight = 400 + "px";

				return img;
			} else {
				var errorText = "Image format not supported: " + format;
				return document.createTextNode(errorText);
			}
		}

		resizeParent();
	};

	view.validate = function() {
		// load from the select
		_value.rowLabels[_rows[0]] = $("#label_select").val();
		return true;
	};

	view.setValidationErrorMessage = function(message) {
	};

	view.getComponentValue = function() {
		return _value;
	};

	return view;

}();