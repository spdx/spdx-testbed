# SPDX-FileCopyrightText: SPDX Contributors
#
# SPDX-License-Identifier: Apache-2.0
from datetime import datetime

import click
from license_expression import get_spdx_licensing
from spdx.model.actor import Actor, ActorType
from spdx.model.annotation import Annotation, AnnotationType
from spdx.model.checksum import Checksum, ChecksumAlgorithm
from spdx.model.document import CreationInfo, Document
from spdx.model.file import File, FileType
from spdx.model.relationship import Relationship, RelationshipType
from spdx.writer.xml.xml_writer import write_document_to_file


@click.command()
@click.option("-target", "-t", help="Path where the generated file will be stored.")
def main(target: str):
    creation_info = CreationInfo(spdx_version="SPDX-2.3", spdx_id="SPDXRef-DOCUMENT", name="document name",
                                 data_license="CC0-1.0", document_namespace="https://some.namespace",
                                 creators=[Actor(ActorType.TOOL, "test-tool")],
                                 created=datetime(2022, 1, 1))
    annotation = Annotation(spdx_id="SPDXRef-somefile", annotation_type=AnnotationType.OTHER,
                            annotation_date=datetime(2022, 1, 1), annotation_comment="File level annotation",
                            annotator=Actor(ActorType.PERSON, "File Annotator"))
    file = File(name="./package/foo.c", spdx_id="SPDXRef-somefile", file_type=[FileType.SOURCE],
                checksums=[Checksum(ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758"),
                           Checksum(ChecksumAlgorithm.MD5, "624c1abb3664f4b35547e7c73864ad24")],
                license_concluded=get_spdx_licensing().parse("GPL-2.0-only"),
                license_info_in_file=[get_spdx_licensing().parse("GPL-2.0-only")],
                license_comment="license comment in file", copyright_text="Copyright 2022 Jane Doe",
                comment="file comment", notice="notice text", contributors=["file contributor"],
                attribution_texts=["file attribution"])
    relationship = Relationship(spdx_element_id="SPDXRef-DOCUMENT", related_spdx_element_id="SPDXRef-somefile",
                                relationship_type=RelationshipType.DESCRIBES)

    doc = Document(creation_info, files=[file], relationships=[relationship], annotations=[annotation])
    write_document_to_file(doc, target)


if __name__ == "__main__":
    main()
