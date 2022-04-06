package info.nightscout.androidaps.plugins.profile.local.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import info.nightscout.androidaps.databinding.LocalprofileDiaFragmentBinding
import info.nightscout.androidaps.plugins.profile.local.events.EventLocalProfileChanged
import info.nightscout.androidaps.utils.HardLimits
import info.nightscout.shared.SafeParse
import java.text.DecimalFormat
import javax.inject.Inject

class LocalProfileDiaFragment : LocalProfileBaseFragment() {
    override val source = "DIA"
    private var _binding: LocalprofileDiaFragmentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    override val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LocalprofileDiaFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val textWatch = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            localProfilePlugin.currentProfile()?.dia = SafeParse.stringToDouble(binding.dia.text)
            binding.insulinGraph.show(activePlugin.activeInsulin, SafeParse.stringToDouble(binding.dia.text))
            localProfilePlugin.isEdited = true
            rxBus.send(EventLocalProfileChanged(source))
        }
    }

    override fun build() {
        val currentProfile = localProfilePlugin.currentProfile() ?: return

        binding.dia.setParams(currentProfile.dia, hardLimits.minDia(), hardLimits.maxDia(), 0.1, DecimalFormat("0.0"), false, null, textWatch)
        binding.dia.editText?.id?.let { binding.diaLabel.labelFor = it }
        binding.dia.tag = "LP_DIA" // For test purposes
        localProfilePlugin.getEditedProfile()?.let {
            binding.insulinGraph.show(activePlugin.activeInsulin, SafeParse.stringToDouble(binding.dia.text))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
