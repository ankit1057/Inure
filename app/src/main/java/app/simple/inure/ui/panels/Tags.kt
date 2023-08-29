package app.simple.inure.ui.panels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import app.simple.inure.R
import app.simple.inure.adapters.ui.AdapterTags
import app.simple.inure.constants.BottomMenuConstants
import app.simple.inure.decorations.overscroll.CustomVerticalRecyclerView
import app.simple.inure.extensions.fragments.ScopedFragment
import app.simple.inure.ui.subpanels.TaggedApps
import app.simple.inure.viewmodels.panels.TagsViewModel

class Tags : ScopedFragment() {

    private lateinit var recyclerView: CustomVerticalRecyclerView

    private var tagsViewModel: TagsViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tags, container, false)

        recyclerView = view.findViewById(R.id.tags_recycler_view)

        tagsViewModel = ViewModelProvider(requireActivity())[TagsViewModel::class.java]

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fullVersionCheck()
        postponeEnterTransition()

        tagsViewModel?.getTags()?.observe(viewLifecycleOwner) {
            val adapter = AdapterTags(it) {
                openFragmentSlide(TaggedApps.newInstance(it), "tagged_apps")
            }

            recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            }

            recyclerView.adapter = adapter

            (view.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }

            bottomRightCornerMenu?.initBottomMenuWithRecyclerView(BottomMenuConstants.getGenericBottomMenuItems(), recyclerView) { id, _ ->
                when (id) {
                    R.drawable.ic_settings -> {
                        openFragmentSlide(Preferences.newInstance(), "preferences")
                    }
                    R.drawable.ic_search -> {
                        openFragmentSlide(Search.newInstance(true), "search")
                    }
                    R.drawable.ic_refresh -> {
                        tagsViewModel?.refresh()
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(): Tags {
            val args = Bundle()
            val fragment = Tags()
            fragment.arguments = args
            return fragment
        }
    }
}