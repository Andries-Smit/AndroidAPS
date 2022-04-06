package info.nightscout.androidaps.plugins.profile.local

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import info.nightscout.androidaps.R
import info.nightscout.androidaps.activities.SingleFragmentActivity
<<<<<<< Updated upstream
import info.nightscout.androidaps.data.ProfileSealed
=======
>>>>>>> Stashed changes
import info.nightscout.androidaps.database.entities.UserEntry.Action
import info.nightscout.androidaps.database.entities.UserEntry.Sources
import info.nightscout.androidaps.database.entities.ValueWithUnit
import info.nightscout.androidaps.databinding.LocalprofileFragmentBinding
import info.nightscout.androidaps.dialogs.ProfileSwitchDialog
import info.nightscout.androidaps.extensions.toVisibility
<<<<<<< Updated upstream
import info.nightscout.androidaps.interfaces.ActivePlugin
import info.nightscout.androidaps.interfaces.GlucoseUnit
import info.nightscout.androidaps.interfaces.Profile
import info.nightscout.androidaps.logging.UserEntryLogger
import info.nightscout.androidaps.plugins.bus.RxBus
=======
>>>>>>> Stashed changes
import info.nightscout.androidaps.plugins.profile.local.events.EventLocalProfileChanged
import info.nightscout.androidaps.plugins.profile.local.fragments.*
import info.nightscout.androidaps.utils.alertDialogs.OKDialog
import info.nightscout.androidaps.utils.protection.ProtectionCheck

class LocalProfileFragment : LocalProfileBaseFragment() {

<<<<<<< Updated upstream
    @Inject lateinit var aapsLogger: AAPSLogger
    @Inject lateinit var rxBus: RxBus
    @Inject lateinit var rh: ResourceHelper
    @Inject lateinit var activePlugin: ActivePlugin
    @Inject lateinit var fabricPrivacy: FabricPrivacy
    @Inject lateinit var localProfilePlugin: LocalProfilePlugin
    @Inject lateinit var hardLimits: HardLimits
    @Inject lateinit var protectionCheck: ProtectionCheck
    @Inject lateinit var dateUtil: DateUtil
    @Inject lateinit var aapsSchedulers: AapsSchedulers
    @Inject lateinit var uel: UserEntryLogger

    private var disposable: CompositeDisposable = CompositeDisposable()
    private var inMenu = false
    private var queryingProtection = false
    private var basalView: TimeListEdit? = null
=======
    override val source = "LOCAL_PROFILE"
    private var _binding: LocalprofileFragmentBinding? = null
    private var inMenu = true
>>>>>>> Stashed changes

    // This property is only valid between onCreateView and onDestroyView.
    override val binding get() = _binding!!

    private val textWatch = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            localProfilePlugin.currentProfile()?.name = binding.name.text.toString()
            doEdit()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LocalprofileFragmentBinding.inflate(inflater, container, false)
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        val tabArray: Array<Pair<String, Fragment>> = arrayOf(
            Pair(rh.gs(R.string.dia_short), LocalProfileDiaFragment()),
            Pair(rh.gs(R.string.ic_short), LocalProfileIcFragment()),
            Pair(rh.gs(R.string.isf_short), LocalProfileIsfFragment()),
            Pair(rh.gs(R.string.basal_short), LocalProfileBasalFragment()),
            Pair(rh.gs(R.string.target_short), LocalProfileTargetFragment())
        )
        val parentClass = this.activity?.let { it::class.java }
        inMenu = parentClass == SingleFragmentActivity::class.java

        val adapter = LocalProfileViewPagerAdapter(tabArray, this.parentFragmentManager, lifecycle)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.tabArray[position].first
        }.attach()

        return binding.root
    }

<<<<<<< Updated upstream
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parentClass = this.activity?.let { it::class.java }
        inMenu = parentClass == SingleFragmentActivity::class.java
        updateProtectedUi()
        // activate DIA tab
        processVisibilityOnClick(binding.diaTab)
        binding.diaPlaceholder.visibility = View.VISIBLE
        // setup listeners
        binding.diaTab.setOnClickListener {
            processVisibilityOnClick(it)
            binding.diaPlaceholder.visibility = View.VISIBLE
        }
        binding.icTab.setOnClickListener {
            processVisibilityOnClick(it)
            binding.ic.visibility = View.VISIBLE
        }
        binding.isfTab.setOnClickListener {
            processVisibilityOnClick(it)
            binding.isf.visibility = View.VISIBLE
        }
        binding.basalTab.setOnClickListener {
            processVisibilityOnClick(it)
            binding.basal.visibility = View.VISIBLE
        }
        binding.targetTab.setOnClickListener {
            processVisibilityOnClick(it)
            binding.target.visibility = View.VISIBLE
        }
        binding.dia.editText?.id?.let { binding.diaLabel.labelFor = it }

        binding.unlock.setOnClickListener { queryProtection() }
=======
    private fun updateProtectedUi() {
        val isLocked = protectionCheck.isLocked(ProtectionCheck.Protection.PREFERENCES)
        binding.mainLayout.visibility = isLocked.not().toVisibility()
        binding.unlock.visibility = isLocked.toVisibility()
    }

    private fun queryProtection() {
        val isLocked = protectionCheck.isLocked(ProtectionCheck.Protection.PREFERENCES)
        if (isLocked) {
            activity?.let { activity ->
                val doUpdate = {
                    activity.runOnUiThread {
                        updateProtectedUi()
                    }
                }
                protectionCheck.queryProtection(activity, ProtectionCheck.Protection.PREFERENCES, doUpdate, doUpdate, doUpdate)
            }
        }
>>>>>>> Stashed changes
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateProtectedUi()
        binding.unlock.setOnClickListener {
            queryProtection()
        }
    }

    override fun onResume() {
        super.onResume()
        if (inMenu) {
            queryProtection()
        } else {
            updateProtectedUi()
        }
    }

    override fun build() {
        if (localProfilePlugin.numOfProfiles == 0) localProfilePlugin.addNewProfile()
        val currentProfile = localProfilePlugin.currentProfile() ?: return

        binding.name.removeTextChangedListener(textWatch)
        binding.name.setText(currentProfile.name)
        binding.name.addTextChangedListener(textWatch)

        context?.let { context ->
            val profileList: ArrayList<CharSequence> = localProfilePlugin.profile?.getProfileList() ?: ArrayList()
            binding.profileList.setAdapter(ArrayAdapter(context, R.layout.spinner_centered, profileList))
            // binding.profileList.setOnTouchListener { _, _ ->
            //     binding.profileList.showDropDown()
            //     false
            // }
        } ?: return
        // binding.profileList.text = currentProfile.name
        binding.profileList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            if (localProfilePlugin.isEdited) {
                activity?.let { activity ->
                    OKDialog.showConfirmation(
                        activity, rh.gs(R.string.doyouwantswitchprofile),
                        {
                            localProfilePlugin.currentProfileIndex = position
                            localProfilePlugin.isEdited = false
                            build()
                        }, null
                    )
                }
            } else {
                localProfilePlugin.currentProfileIndex = position
                build()
            }
        }

        binding.profileAdd.setOnClickListener {
            if (localProfilePlugin.isEdited) {
                activity?.let { OKDialog.show(it, "", rh.gs(R.string.saveorresetchangesfirst)) }
            } else {
                uel.log(Action.NEW_PROFILE, Sources.LocalProfile)
                localProfilePlugin.addNewProfile()
                build()
            }
        }

        binding.profileClone.setOnClickListener {
            if (localProfilePlugin.isEdited) {
                activity?.let { OKDialog.show(it, "", rh.gs(R.string.saveorresetchangesfirst)) }
            } else {
                uel.log(
                    Action.CLONE_PROFILE, Sources.LocalProfile, ValueWithUnit.SimpleString(
                        localProfilePlugin.currentProfile()?.name ?: ""
                    )
                )
                localProfilePlugin.cloneProfile()
                build()
            }
        }

        binding.profileRemove.setOnClickListener {
            activity?.let { activity ->
                OKDialog.showConfirmation(activity, rh.gs(R.string.deletecurrentprofile), {
                    uel.log(
                        Action.PROFILE_REMOVED, Sources.LocalProfile, ValueWithUnit.SimpleString(
                            localProfilePlugin.currentProfile()?.name
                                ?: ""
                        )
                    )
                    localProfilePlugin.removeCurrentProfile()
                    build()
                }, null)
            }
        }

        // this is probably not possible because it leads to invalid profile
        // if (!pumpDescription.isTempBasalCapable) binding.basal.visibility = View.GONE

        @Suppress("SetTextI18n")
        binding.units.text = rh.gs(R.string.units_colon) + " " + (if (currentProfile.mgdl) rh.gs(R.string.mgdl) else rh.gs(R.string.mmol))

        binding.profileswitch.setOnClickListener {
            ProfileSwitchDialog()
                .also { it.arguments = Bundle().also { bundle -> bundle.putString("profileName", localProfilePlugin.currentProfile()?.name) } }
                .show(childFragmentManager, "ProfileSwitchDialog")
        }

        binding.reset.setOnClickListener {
            localProfilePlugin.loadSettings()
            build()
            rxBus.send(EventLocalProfileChanged(source))
        }

        binding.save.setOnClickListener {
            if (!localProfilePlugin.isValidEditState(activity)) {
                return@setOnClickListener // Should not happen as saveButton should not be visible if not valid
            }
            uel.log(
                Action.STORE_PROFILE, Sources.LocalProfile, ValueWithUnit.SimpleString(
                    localProfilePlugin.currentProfile()?.name
                        ?: ""
                )
            )
            localProfilePlugin.storeSettings(activity)
            build()
        }
        updateGUI()
    }

<<<<<<< Updated upstream
    @Synchronized
    override fun onResume() {
        super.onResume()
        if (inMenu) queryProtection() else updateProtectedUi()
        disposable += rxBus
            .toObservable(EventLocalProfileChanged::class.java)
            .observeOn(aapsSchedulers.main)
            .subscribe({ build() }, fabricPrivacy::logException)
        build()
    }

    @Synchronized
    override fun onPause() {
        super.onPause()
        disposable.clear()
    }

    @Synchronized
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

=======
>>>>>>> Stashed changes
    fun doEdit() {
        localProfilePlugin.isEdited = true
        updateGUI()
    }

    private fun updateGUI() {
        if (_binding == null) return
        val isValid = localProfilePlugin.isValidEditState(activity)
        val isEdited = localProfilePlugin.isEdited
        if (isValid) {
<<<<<<< Updated upstream
            this.view?.setBackgroundColor(rh.gac(context, R.attr.okBackgroundColor))
=======
            this.view?.setBackgroundColor(rh.gac(R.attr.okBackground))
>>>>>>> Stashed changes
            binding.profileList.isEnabled = true
            binding.profileswitch.visibility = isEdited.not().toVisibility()
            binding.save.visibility = isEdited.toVisibility()
        } else {
<<<<<<< Updated upstream
            this.view?.setBackgroundColor(rh.gac(context, R.attr.errorBackgroundColor))
=======
            this.view?.setBackgroundColor(rh.gac(R.attr.errorBackground))
>>>>>>> Stashed changes
            binding.profileList.isEnabled = false
            binding.profileswitch.visibility = View.GONE
            binding.save.visibility = View.GONE // don't save an invalid profile
        }

        // Show reset button if data was edited
        binding.reset.visibility = isEdited.toVisibility()
    }

<<<<<<< Updated upstream
    private fun processVisibilityOnClick(selected: View) {
        binding.diaTab.setBackgroundColor(rh.gac(context, R.attr.defaultbackground))
        binding.icTab.setBackgroundColor(rh.gac(context, R.attr.defaultbackground))
        binding.isfTab.setBackgroundColor(rh.gac(context, R.attr.defaultbackground))
        binding.basalTab.setBackgroundColor(rh.gac(context, R.attr.defaultbackground))
        binding.targetTab.setBackgroundColor(rh.gac(context, R.attr.defaultbackground))
        selected.setBackgroundColor(rh.gac(context, R.attr.tabBgColorSelected))
        binding.diaPlaceholder.visibility = View.GONE
        binding.ic.visibility = View.GONE
        binding.isf.visibility = View.GONE
        binding.basal.visibility = View.GONE
        binding.target.visibility = View.GONE
    }

    private fun updateProtectedUi() {
        val isLocked = protectionCheck.isLocked(ProtectionCheck.Protection.PREFERENCES)
        binding.mainLayout.visibility = isLocked.not().toVisibility()
        binding.unlock.visibility = isLocked.toVisibility()
    }

    private fun queryProtection() {
        val isLocked = protectionCheck.isLocked(ProtectionCheck.Protection.PREFERENCES)
        if (isLocked && !queryingProtection) {
            activity?.let { activity ->
                queryingProtection = true
                val doUpdate = { activity.runOnUiThread { queryingProtection = false; updateProtectedUi() } }
                protectionCheck.queryProtection(activity, ProtectionCheck.Protection.PREFERENCES, doUpdate, doUpdate, doUpdate)
            }
        }
    }
=======
    @Synchronized
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

>>>>>>> Stashed changes
}
