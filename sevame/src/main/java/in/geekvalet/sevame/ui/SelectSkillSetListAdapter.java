package in.geekvalet.sevame.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import in.geekvalet.sevame.R;
import in.geekvalet.sevame.model.Service;
import in.geekvalet.sevame.model.ServiceProvider;
import in.geekvalet.sevame.model.Skill;

/**
 * Created by gautam on 8/12/14.
 */
public class SelectSkillSetListAdapter extends BaseExpandableListAdapter {
    private final List<Service> services;
    private final ServiceProvider serviceProvider;
    private Context context;
    private final HashMap<Service, ArrayList<CheckBox>> checkboxes;

    public SelectSkillSetListAdapter(Context context, List<Service> services, ServiceProvider serviceProvider) {
        this.context = context;
        this.services = services;
        this.checkboxes = new HashMap<Service, ArrayList<CheckBox>>();
        this.serviceProvider = serviceProvider;
    }

    public Map<String, List<Skill>> selectedSkills() {
        HashMap<String, List<Skill>> results = new HashMap<String, List<Skill>>();

        for(Service service : this.checkboxes.keySet()) {
            List<Skill> skills = new ArrayList<Skill>();

            for(CheckBox checkbox: this.checkboxes.get(service)) {
                if(checkbox.isChecked()) {
                    skills.add(new Skill(String.valueOf(checkbox.getText()), true));
                }
            }

            if(skills.size() > 0) {
                results.put(service.getName(), skills);
            }
        }

        return results;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.services.get(groupPosition).getSkills().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);
        final String serviceName = this.services.get(groupPosition).getName();
        final Set<String> existingSkills = fetchExistingSkills(serviceName);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.fragment_skillset_item, null);
        }

        CheckBox skillCheckbox = (CheckBox) convertView.findViewById(R.id.skill);

        if(existingSkills.contains(childText)) {
            skillCheckbox.setChecked(true);
        }

        skillCheckbox.setText(childText);
        this.checkboxes.get(this.services.get(groupPosition)).add(skillCheckbox);
        return convertView;
    }

    private Set<String> fetchExistingSkills(String serviceName) {
        Set<String> skills = new HashSet<String>();

        for(Skill skill: this.serviceProvider.getSkills().get(serviceName)) {
            skills.add(skill.getName());
        }

        return skills;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.services.get(groupPosition).getSkills().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.services.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.services.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Service service = (Service) getGroup(groupPosition);
        String headerTitle = service.getName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.fragment_skillset_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        this.checkboxes.put(service, new ArrayList<CheckBox>());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
