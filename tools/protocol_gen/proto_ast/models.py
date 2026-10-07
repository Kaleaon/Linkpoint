from dataclasses import dataclass, field
from typing import Any


@dataclass
class FieldSpec:
    name: str
    type_name: str  # e.g., U8, U16, U32, U64, S8, S16, S32, S64, F32, F64, LLUUID, BOOL, IPADDR, IPPORT, Variable1, Variable2, Fixed, Vector3, Vector3d, Vector4, Quaternion
    count: int = 1  # For array or fixed string size


@dataclass
class BlockSpec:
    name: str
    block_type: str  # Single, Multiple, Variable
    count: int = 1  # Default count for Multiple blocks
    fields: list[FieldSpec] = field(default_factory=list)


@dataclass
class MessageSpec:
    name: str
    frequency: str  # High, Medium, Low, Fixed
    message_number: int
    trust_level: str  # Trusted, NotTrusted
    encoding: str  # Zerocoded, Unencoded
    flags: list[str] = field(default_factory=list)  # e.g. UDPBlackListed, UDPDeprecated
    blocks: list[BlockSpec] = field(default_factory=list)


@dataclass
class PropertySpec:
    name: str
    data_type: str  # string, integer, number, boolean, uuid, object, array
    required: bool = False
    description: str = ""
    format: str | None = None
    items_type: str | None = None
    enum_values: list[Any] = field(default_factory=list)


@dataclass
class LLSDSchemaSpec:
    id: str
    title: str
    description: str
    schema_type: str
    properties: dict[str, PropertySpec] = field(default_factory=dict)
    required: list[str] = field(default_factory=list)


@dataclass
class ProtocolAST:
    version: str = "2.0"
    messages: list[MessageSpec] = field(default_factory=list)
    llsd_schemas: list[LLSDSchemaSpec] = field(default_factory=list)

    def to_dict(self) -> dict[str, Any]:
        return {
            "version": self.version,
            "messages_count": len(self.messages),
            "llsd_schemas_count": len(self.llsd_schemas),
            "messages": [
                {
                    "name": m.name,
                    "frequency": m.frequency,
                    "number": m.message_number,
                    "trust": m.trust_level,
                    "encoding": m.encoding,
                    "flags": m.flags,
                    "blocks": [
                        {
                            "name": b.name,
                            "type": b.block_type,
                            "count": b.count,
                            "fields": [
                                {"name": f.name, "type": f.type_name, "count": f.count}
                                for f in b.fields
                            ],
                        }
                        for b in m.blocks
                    ],
                }
                for m in self.messages
            ],
            "llsd_schemas": [
                {
                    "id": s.id,
                    "title": s.title,
                    "description": s.description,
                    "properties": {
                        p_name: {
                            "type": p.data_type,
                            "required": p.required,
                            "format": p.format,
                            "items_type": p.items_type,
                        }
                        for p_name, p in s.properties.items()
                    },
                }
                for s in self.llsd_schemas
            ],
        }
