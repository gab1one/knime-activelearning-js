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
	var _port = 8042;
	var _host = "localhost";

	var minWidth = 400;
	var minHeight = 300;
	var defaultFont = "sans-serif";
	var defaultFontSize = 12;


	view.name = "knime_al_loopend";

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


	view.validate = function() {
		return true;
	};

	view.setValidationErrorMessage = function(message) {
	};

	view.value = function() {
		return null;
	};

	return view;
	
}();
