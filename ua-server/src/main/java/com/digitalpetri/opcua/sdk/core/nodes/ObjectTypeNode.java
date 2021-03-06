/*
 * digitalpetri OPC-UA SDK
 *
 * Copyright (C) 2015 Kevin Herron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.digitalpetri.opcua.sdk.core.nodes;

public interface ObjectTypeNode extends Node {

    /**
     * The IsAbstract attribute indicates if this ObjectType is abstract or not.
     *
     * @return {@code true} if this ObjectType is abstract.
     */
    Boolean getIsAbstract();

    /**
     * Set the IsAbstract attribute of this ObjectType.
     *
     * @param isAbstract {@code true} if this
     */
    void setIsAbstract(boolean isAbstract);

//    Optional<String> getNodeVersion();
//
//    Optional<ByteString> getIcon();
//
//    void setNodeVersion(Optional<String> nodeVersion);
//
//    void setIcon(Optional<ByteString> icon);

}
