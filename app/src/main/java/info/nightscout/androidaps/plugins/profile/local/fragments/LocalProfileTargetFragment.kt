package info.nightscout.androidaps.plugins.profile.local.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import info.nightscout.androidaps.R
import info.nightscout.androidaps.data.ProfileSealed
import info.nightscout.androidaps.databinding.LocalprofileTargetFragmentBinding
import info.nightscout.androidaps.interfaces.GlucoseUnit
import info.nightscout.androidaps.interfaces.Profile
import info.nightscout.androidaps.plugins.profile.local.LocalProfilePlugin
import info.nightscout.androidaps.plugins.profile.local.events.EventLocalProfileChanged
import info.nightscout.androidaps.utils.HardLimits
import info.nightscout.androidaps.utils.Round
import info.nightscout.androidaps.utils.ui.TimeListEdit
import java.text.DecimalFormat

class LocalProfileTargetFragment : LocalProfileBaseFragment() {

    override val source = "TARGET"
    private var _binding: LocalprofileTargetFragmentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    override val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LocalprofileTargetFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val save = Runnable {
        localProfilePlugin.getEditedProfile()?.let {
            binding.targetGraph.show(ProfileSealed.Pure(it))
        }
        localProfilePlugin.isEdited = true
        rxBus.send(EventLocalProfileChanged(source))
    }

    private fun getUnitRange(range: DoubleArray, profile: LocalProfilePlugin.SingleProfile): DoubleArray {
        if (profile.mgdl) {
            return range
        }
        return doubleArrayOf(
            Round.roundUp(Profile.fromMgdlToUnits(range[0], GlucoseUnit.MMOL), 1),
            Round.roundDown(Profile.fromMgdlToUnits(range[1], GlucoseUnit.MMOL), 1)
        )
    }

    override fun build() {
        val currentProfile = localProfilePlugin.currentProfile() ?: return

        val limitMin = getUnitRange(HardLimits.VERY_HARD_LIMIT_MIN_BG, currentProfile)
        val limitMax = getUnitRange(HardLimits.VERY_HARD_LIMIT_MAX_BG, currentProfile)
        TimeListEdit(
            context,
            aapsLogger,
            dateUtil,
            view,
            R.id.target_holder,
            "TARGET",
            rh.gs(R.string.target_long_label),
            currentProfile.targetLow,
            currentProfile.targetHigh,
            limitMin,
            limitMax,
            1.0,
            DecimalFormat("0"),
            save
        )

        localProfilePlugin.getEditedProfile()?.let {
            binding.targetGraph.show(ProfileSealed.Pure(it))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
