/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra OAuth Social Extension
 * Copyright (C) 2019 Synacor, Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.oauth.cache.ephemeral;

import com.zimbra.cs.ephemeral.EphemeralInput;
import com.zimbra.cs.ephemeral.EphemeralLocation;
import com.zimbra.cs.ephemeral.ValueEncoder;
import com.zimbra.oauth.utilities.OAuth2Constants;

/**
 * The OAuth2ValueEncoder class.
 *
 * @author Zimbra API Team
 * @package com.zimbra.oauth.cache.ephemeral
 * @copyright Copyright © 2019
 * @see SSDBValueEncoder
 */
public class OAuth2ValueEncoder extends ValueEncoder {

    protected final String DELIMITER = OAuth2Constants.CACHE_VALUE_DELIMITER.getValue();

    @Override
    public String encodeValue(EphemeralInput input, EphemeralLocation target) {
        if(input == null || input.getValue() == null) {
            return null;
        }
        final Long expires = input.getExpiration();
        final String value = input.getValue().toString();
        if (expires != null && expires > 0L) {
            return String.format("%s%s%s", value, DELIMITER, String.valueOf(expires));
        } else {
            return String.format("%s%s", value, DELIMITER);
        }
    }

}

