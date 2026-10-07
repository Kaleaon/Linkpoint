"""
Parser package containing TemplateParser and LLSDSchemaParser.
"""

from tools.protocol_gen.parser.llsd_schema_parser import LLSDSchemaParser
from tools.protocol_gen.parser.template_parser import TemplateParser

__all__ = ["LLSDSchemaParser", "TemplateParser"]
