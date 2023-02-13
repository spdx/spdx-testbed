# SPDX-FileCopyrightText: SPDX Contributors
#
# SPDX-License-Identifier: Apache-2.0
from datetime import datetime

import click
from spdx.model.actor import Actor, ActorType
from spdx.model.annotation import Annotation, AnnotationType
from spdx.model.checksum import Checksum, ChecksumAlgorithm
from spdx.model.document import CreationInfo, Document
from spdx.model.external_document_ref import ExternalDocumentRef
from spdx.model.file import File
from spdx.model.relationship import Relationship, RelationshipType
from spdx.model.version import Version
from spdx.writer.xml.xml_writer import write_document_to_file


@click.command()
@click.option("-target", "-t", help="Path where the generated file will be stored.")
def main(target: str):
    external_document_ref = ExternalDocumentRef(document_ref_id="DocumentRef-externaldocumentid",
                                                document_uri="http://external.uri",
                                                checksum=Checksum(ChecksumAlgorithm.SHA1,
                                                                  value="d6a770ba38583ed4bb4525bd96e50461655d2758"))
    creation_info = CreationInfo(spdx_version="SPDX-2.3", spdx_id="SPDXRef-DOCUMENT", name="document name",
                                 document_namespace="https://some.namespace", data_license="CC0-1.0",
                                 creators=[Actor(ActorType.PERSON, "Jane Doe", "jane.doe@example.com"),
                                           Actor(ActorType.TOOL, "test-tool")],
                                 created=datetime(2022, 1, 1), license_list_version=Version(3, 7),
                                 creator_comment="creation comment", document_comment="document comment",
                                 external_document_refs=[external_document_ref])
    annotation = Annotation(spdx_id="SPDXRef-DOCUMENT", annotation_type=AnnotationType.REVIEW,
                            annotation_date=datetime(2022, 1, 1), annotation_comment="Document level annotation",
                            annotator=Actor(ActorType.PERSON, "Document Reviewer", "mail@mail.com"))
    file = File(name="./foo.txt", spdx_id="SPDXRef-somefile",
                checksums=[Checksum(ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758")])
    relationship = Relationship(spdx_element_id="SPDXRef-DOCUMENT", related_spdx_element_id="SPDXRef-somefile",
                                relationship_type=RelationshipType.DESCRIBES)

    doc = Document(creation_info, files=[file], relationships=[relationship], annotations=[annotation])
    write_document_to_file(doc, target)


if __name__ == "__main__":
    main()
