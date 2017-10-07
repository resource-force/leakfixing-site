/*
 * leakfixing-site: a site to show and manage a gas leak database
 * Copyright (C) 2017 Kevin\ Liu
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

package info.gasleaks.site.data

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import java.time.LocalDate
import javax.persistence.*

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator::class, property = "id")
data class Leak(
        @Id
        val id: Long,
        @ManyToOne
        @JsonIgnore
        val source: Dataset,
        val location: String,
        val status: LeakStatus,
        val size: Int?,
        val reportedOn: LocalDate?,
        val fixedOn: LocalDate?)