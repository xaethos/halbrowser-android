package net.xaethos.android.halbrowser.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.xaethos.android.halbrowser.R;
import net.xaethos.android.halbrowser.Relation;
import net.xaethos.android.halparser.HALLink;
import net.xaethos.android.halparser.HALProperty;
import net.xaethos.android.halparser.HALResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleRepresentationAdapter extends MergeAdapter
{

    private static final String NAME = "NAME";
    private static final String VALUE = "VALUE";

    private static final String[] FROM = { NAME, VALUE };
    private static final int[] TO = { android.R.id.text1, android.R.id.text2 };

    public SimpleRepresentationAdapter(Context context, HALResource resource, Bundle arguments) {
        super();

        Set<String> rels = Sets.newLinkedHashSet();

        rels.addAll(resource.getResourceRels());
        rels.addAll(resource.getLinkRels());

        List<Map<String, Object>> propertyData = Lists.newArrayList();
        for (HALProperty property : resource.getProperties()) {
            Map<String, Object> data = Maps.newHashMap();
            data.put(NAME, property.getName());
            data.put(VALUE, property.getValue());
            propertyData.add(data);
        }
        if (!propertyData.isEmpty()) {
            addAdapter(new PropertyAdapter(context, propertyData));
        }

        Bundle relViewable = arguments.getBundle("rel_show_map");
        for (String rel : rels) {
            if (!relViewable.getBoolean(rel, true)) continue;

            List<HALResource> resources = Lists.newArrayList(resource.getResources(rel));
            if (resources != null && !resources.isEmpty()) {
                addAdapter(new ResourceAdapter(context, resources));
            }

            Collection<HALLink> links = resource.getLinks(rel);
            if (links != null && !links.isEmpty()) {
                addAdapter(LinkAdapter.forLinks(context, links));
            }
        }

    }

    // ***** Inner classes

    class PropertyAdapter extends SimpleAdapter
    {

        public PropertyAdapter(Context context, List<Map<String, Object>> data) {
            super(context, data, android.R.layout.simple_list_item_2, FROM, TO);
        }

    }

    class ResourceAdapter extends ArrayAdapter<HALResource>
    {

        public ResourceAdapter(Context context, List<HALResource> resources) {
            super(context, android.R.layout.simple_list_item_1, resources);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layout;

            if (convertView == null) {
                layout = (LinearLayout) LayoutInflater.from(getContext())
                                                      .inflate(R.layout.embedded_item, parent, false);
            }
            else {
                layout = (LinearLayout) convertView;
            }
            HALResource resource = getItem(position);
            HALLink link = resource.getLink(Relation.SELF);
            String title = link.getTitle();

            ((TextView) layout.findViewById(R.id.title)).setText(title);
            LinearLayout innerLayout = (LinearLayout) layout.findViewById(R.id.properties);

            innerLayout.removeAllViews();
            for (HALProperty property : resource.getProperties()) {
                Object value = property.getValue();
                if (value == null) continue;
                TextView tv = new TextView(getContext());
                tv.setText(value.toString());
                innerLayout.addView(tv);
            }

            return layout;
        }

    }

    static class LinkAdapter extends ArrayAdapter<HALLink>
    {
        public static LinkAdapter forLinks(Context context, Collection<HALLink> links) {
            List<HALLink> viewableLinks = new ArrayList<HALLink>();
            for (HALLink link : links) {
                if (link.getTitle() != null) viewableLinks.add(link);
            }

            return new LinkAdapter(context, viewableLinks);
        }

        public LinkAdapter(Context context, List<HALLink> links) {
            super(context, android.R.layout.simple_list_item_1, links);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1,
                                                                        parent,
                                                                        false);
            }
            HALLink link = getItem(position);
            String title = link.getTitle();

            ((TextView) convertView.findViewById(android.R.id.text1)).setText(title);
            return convertView;
        }
    }

}
