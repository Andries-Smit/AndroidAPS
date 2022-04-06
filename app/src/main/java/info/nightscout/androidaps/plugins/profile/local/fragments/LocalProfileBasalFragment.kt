package info.nightscout.androidaps.plugins.profile.local.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import info.nightscout.androidaps.R
import info.nightscout.androidaps.data.ProfileSealed
import info.nightscout.androidaps.databinding.LocalprofileBasalFragmentBinding
import info.nightscout.androidaps.plugins.profile.local.events.EventLocalProfileChanged
import info.nightscout.androidaps.utils.DecimalFormatter
import info.nightscout.androidaps.utils.ui.TimeListEdit
import java.text.DecimalFormat

class LocalProfileBasalFragment : LocalProfileBaseFragment() {

    private var _binding: LocalprofileBasalFragmentBinding? = null
    override val source = "BASAL"

    // This property is only valid between onCreateView and onDestroyView.
    override val binding get() = _binding!!

    private var basalView: TimeListEdit? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LocalprofileBasalFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun sumLabel(): String {
        val profile = localProfilePlugin.getEditedProfile()
        val sum = profile?.let { ProfileSealed.Pure(profile).baseBasalSum() } ?: 0.0
        return " ∑" + DecimalFormatter.to2Decimal(sum) + rh.gs(R.string.insulin_unit_shortname)
    }

    private val save = Runnable {
        localProfilePlugin.isEdited = true
        basalView?.updateLabel(rh.gs(R.string.basal_label) + ": " + sumLabel())
        localProfilePlugin.getEditedProfile()?.let {
            binding.basalGraph.show(ProfileSealed.Pure(it))
        }
        rxBus.send(EventLocalProfileChanged(source))
    }

    override fun build() {
        val currentProfile = localProfilePlugin.currentProfile() ?: return

        val pumpDescription = activePlugin.activePump.pumpDescription
        val range = doubleArrayOf(pumpDescription.basalMinimumRate, pumpDescription.basalMaximumRate)
        val label = rh.gs(R.string.basal_long_label) + ": " + sumLabel()
        basalView = TimeListEdit(context, aapsLogger, dateUtil, view, R.id.basal_holder, "BASAL", label, currentProfile.basal, null, range, null, 0.01, DecimalFormat("0.00"), save)
        localProfilePlugin.getEditedProfile()?.let {
            binding.basalGraph.show(ProfileSealed.Pure(it))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
