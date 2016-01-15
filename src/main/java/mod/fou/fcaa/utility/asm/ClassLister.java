/*
 * Copyright (c) 2016 Foudroyant Factotum
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 */
package mod.fou.fcaa.utility.asm;

import net.minecraft.launchwrapper.IClassTransformer;

public class ClassLister implements IClassTransformer
{
    @Override
    public byte[] transform(String s, String s1, byte[] bytes)
    {
        if (s.startsWith("mod.fou.fcaa"))
            System.out.println(s);

        return bytes;
    }
}
