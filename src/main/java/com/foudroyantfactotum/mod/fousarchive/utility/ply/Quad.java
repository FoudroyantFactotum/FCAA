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

import java.util.Arrays;

public class Quad
{
    public final float[] a;
    public final float[] b;
    public final float[] c;
    public final float[] d;

    public Quad(float[] a, float[] b, float[] c, float[] d)
    {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public String toString()
    {
        return "Quad{" +
                "a=" + Arrays.toString(a) +
                ", b=" + Arrays.toString(b) +
                ", c=" + Arrays.toString(c) +
                ", d=" + Arrays.toString(d) +
                '}';
    }
}
