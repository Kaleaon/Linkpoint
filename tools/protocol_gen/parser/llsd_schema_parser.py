import json
import os
from typing import List, Optional
from tools.protocol_gen.proto_ast.models import LLSDSchemaSpec, PropertySpec, ProtocolAST

class LLSDSchemaParser:
    """
    Parser for LLSD JSON Schemas into ProtocolAST LLSDSchemaSpecs.
    """

    def parse_file(self, file_path: str, ast: Optional[ProtocolAST] = None) -> ProtocolAST:
        if ast is None:
            ast = ProtocolAST()

        with open(file_path, "r", encoding="utf-8") as f:
            data = json.load(f)

        schema_id = data.get("$id", os.path.basename(file_path))
        title = data.get("title", os.path.basename(file_path).split(".")[0])
        description = data.get("description", "")
        schema_type = data.get("type", "object")
        required_list = data.get("required", [])

        properties_map = {}
        props_data = data.get("properties", {})
        for prop_name, prop_data in props_data.items():
            prop_type = prop_data.get("type", "string")
            prop_format = prop_data.get("format")
            prop_desc = prop_data.get("description", "")
            is_req = prop_name in required_list
            items_type = None
            if prop_type == "array" and "items" in prop_data:
                items_type = prop_data["items"].get("type", "string")

            enum_vals = prop_data.get("enum", [])

            properties_map[prop_name] = PropertySpec(
                name=prop_name,
                data_type=prop_type,
                required=is_req,
                description=prop_desc,
                format=prop_format,
                items_type=items_type,
                enum_values=enum_vals
            )

        spec = LLSDSchemaSpec(
            id=schema_id,
            title=title,
            description=description,
            schema_type=schema_type,
            properties=properties_map,
            required=required_list
        )

        ast.llsd_schemas.append(spec)
        return ast

    def parse_directory(self, dir_path: str, ast: Optional[ProtocolAST] = None) -> ProtocolAST:
        if ast is None:
            ast = ProtocolAST()

        if not os.path.exists(dir_path):
            return ast

        for root, dirs, files in sorted(os.walk(dir_path)):
            dirs.sort()
            for file_name in sorted(files):
                if file_name.endswith(".json"):
                    full_path = os.path.join(root, file_name)
                    self.parse_file(full_path, ast)

        return ast
