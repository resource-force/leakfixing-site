/*
 * leakfixing-site: a site to show and manage a gas leak database
 * Copyright (C) 2017 Kevin Liu
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ing.leakfix.site

import ing.leakfix.site.data.DataValidityRange
import ing.leakfix.site.data.GasLeak
import ing.leakfix.site.data.GasLeakSource
import ing.leakfix.site.data.GasLeakStatus
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest
class GasLeakTest {
    private final val NGRID_SOURCE = GasLeakSource(
            vendor = "NGRID",
            dataset = "Unrepaired 2016",
            dataBetween = DataValidityRange(
                    LocalDate.of(2016, 1, 1),
                    LocalDate.of(2016, 12, 31)))
    private final val HEET_SOURCE = GasLeakSource(
            vendor = "MAPC-HEET",
            dataset = "Study 2016",
            dataBetween = DataValidityRange(
                    LocalDate.of(2016, 1, 1),
                    LocalDate.of(2016, 12, 31)))

    private final val NGRID_REFERENCE_LEAK = GasLeak(
            location = "18 Piper Road, Acton MA 01720",
            sources = listOf(NGRID_SOURCE),
            size = null,
            status = GasLeakStatus.UNREPAIRED,
            reportedOn = LocalDate.of(2001, 1, 1),
            fixedOn = null,
            ngridId = Random().nextInt())
    private final val HEET_REFERENCE_LEAK = NGRID_REFERENCE_LEAK
            .copy(sources = listOf(HEET_SOURCE))
    private final val MERGED_REFERENCE_LEAK = NGRID_REFERENCE_LEAK
            .copy(sources = listOf(NGRID_SOURCE, HEET_SOURCE))

    @Test
    fun mergeFlag() {
        Assert.assertTrue(MERGED_REFERENCE_LEAK.merged)
        Assert.assertFalse(NGRID_REFERENCE_LEAK.merged)
        Assert.assertFalse(HEET_REFERENCE_LEAK.merged)
    }

    @Test
    fun mergeCombinesSources() = Assert.assertEquals(
                MERGED_REFERENCE_LEAK,
                NGRID_REFERENCE_LEAK.mergeWith(HEET_REFERENCE_LEAK))

    @Test
    fun mergeSelectsProperStatus() {
        Assert.assertEquals(
                MERGED_REFERENCE_LEAK.copy(status = GasLeakStatus.FIXED),
                NGRID_REFERENCE_LEAK.copy(status = GasLeakStatus.FIXED).mergeWith(HEET_REFERENCE_LEAK))
        Assert.assertEquals(
                MERGED_REFERENCE_LEAK.copy(status = GasLeakStatus.UNREPAIRED),
                NGRID_REFERENCE_LEAK.copy(status = GasLeakStatus.MISSING).mergeWith(HEET_REFERENCE_LEAK))
        Assert.assertEquals(
                MERGED_REFERENCE_LEAK.copy(status = GasLeakStatus.FIXED),
                NGRID_REFERENCE_LEAK.copy(status = GasLeakStatus.MISSING)
                        .mergeWith(HEET_REFERENCE_LEAK.copy(status = GasLeakStatus.FIXED)))
    }

    @Test
    fun mergeSelectsProperDate() = Assert.assertEquals(
            MERGED_REFERENCE_LEAK.copy(
                    reportedOn = LocalDate.of(2001, 1, 1),
                    fixedOn = LocalDate.of(2001, 1, 2)),
            NGRID_REFERENCE_LEAK.copy(
                    reportedOn = LocalDate.of(2001, 1, 1),
                    fixedOn = LocalDate.of(2001, 1, 2))
                    .mergeWith(HEET_REFERENCE_LEAK.copy(
                            reportedOn = LocalDate.of(2001, 1, 2),
                            fixedOn = LocalDate.of(2001, 1, 2))))
}
