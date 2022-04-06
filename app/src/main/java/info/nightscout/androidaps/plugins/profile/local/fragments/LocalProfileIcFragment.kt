package info.nightscout.androidaps.plugins.profile.local.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import info.nightscout.androidaps.R
import info.nightscout.androidaps.data.ProfileSealed
import info.nightscout.androidaps.databinding.LocalprofileIcFragmentBinding
import info.nightscout.androidaps.plugins.profile.local.events.EventLocalProfileChanged
import info.nightscout.androidaps.utils.ui.TimeListEdit
import java.text.DecimalFormat

class LocalProfileIcFragment : LocalProfileBaseFragment() {

    private var _binding: LocalprofileIcFragmentBinding? = null
    override val source = "IC"
    // This property is only valid between onCreateView and onDestroyView.
    override val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LocalprofileIcFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val save = Runnable {
        localProfilePlugin.isEdited = true
        localProfilePlugin.getEditedProfile()?.let {
            binding.icGraph.show(ProfileSealed.Pure(it))
        }
        rxBus.send(EventLocalProfileChanged(source))
    }

    override fun build() {
        val currentProfile = localProfilePlugin.currentProfile() ?: return

        val range = doubleArrayOf(hardLimits.minIC(), hardLimits.maxIC())
        TimeListEdit(context, aapsLogger, dateUtil, view, R.id.ic_holder, "IC", rh.gs(R.string.ic_long_label), currentProfile.ic, null, range, null, 0.1, DecimalFormat("0.0"), save)
        localProfilePlugin.getEditedProfile()?.let {
            binding.icGraph.show(ProfileSealed.Pure(it))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
