import constants from "@/utils/constants";
const fieldHeaders = [
  { key: "fieldName", title: "Field Name", type: "text" },
  { key: "caption", title: "Caption", type: "text" },
  {
    key: "dataType", title: "Data Type", type: "select",
    namedValues: ["String", "List", "Set", "Image", "LocalDate", "LocalDateTime",
      "LocalTime", "boolean", "int", "long", "float", "double", "BigDecimal", "File"]
  },
  { key: "references", title: "References", type: "text" },
  {
    key: "mapping", title: "Mapping", type: "select",
    namedValues: ['OneToOne', 'ManyToOne', 'OneToMany', 'ManyToMany'],
    projectType: [constants.projectTypes.javaFx, constants.projectTypes.springBoot],
  },
  { key: "key", title: "Key", type: "select", namedValues: ['Primary', 'Primary_Auto', 'Foreign', 'Unique', 'Unique_Group'], projectType: [constants.projectTypes.javaFx, constants.projectTypes.springBoot] },
  {
    key: "saburiKey", title: "Saburi Key", type: "select",
    namedValues: ['ID_Helper', 'ID_Generator', 'Display', 'UI_Only', 'Query_Only', 'Read_Only']
  },
  { key: "size", title: "Size", type: "text" },
  { key: "nullable", title: "Nullable", type: "checkbox", projectType: [constants.projectTypes.javaFx, constants.projectTypes.springBoot] },
  { key: "enumerated", title: "Enum", type: "checkbox" },
  { key: "projectName", title: "Project", type: "text", projectType: [constants.projectTypes.springBoot] },
  { key: "expose", title: "Expose", type: "checkbox" },
  { key: "moduleName", title: "Module", type: "text", projectType: [constants.projectTypes.vue] },
  { key: "select", title: "Select", type: "checkbox", projectType: [constants.projectTypes.vue] },
  { key: "subFields", title: "Sub Fields", type: "text", projectType: [constants.projectTypes.vue] },

];

export default fieldHeaders;
