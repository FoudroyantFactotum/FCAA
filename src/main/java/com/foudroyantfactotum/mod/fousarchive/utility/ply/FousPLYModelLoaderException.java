/*
 * Fou's Archive - A Minecraft Mod
 * Copyright (c) 2016 Foudroyant Factotum
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foudroyantfactotum.mod.fousarchive.utility.ply;

import com.foudroyantfactotum.mod.fousarchive.utility.FousArchiveException;

public class FousPLYModelLoaderException extends FousArchiveException
{
    public FousPLYModelLoaderException() {}

    public FousPLYModelLoaderException(String s) { super(s); }

    public FousPLYModelLoaderException(String s, Throwable throwable) { super(s, throwable); }

    public FousPLYModelLoaderException(Throwable throwable) { super(throwable); }

    public FousPLYModelLoaderException(String s, Throwable throwable, boolean b, boolean b1) { super(s, throwable, b, b1); }
}
