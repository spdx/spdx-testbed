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
from spdx.model.file import File
from spdx.model.package import (ExternalPackageRef, ExternalPackageRefCategory,
                                Package, PackagePurpose,
                                PackageVerificationCode)
from spdx.model.relationship import Relationship, RelationshipType
from spdx.writer.xml.xml_writer import write_document_to_file


@click.command()
@click.option("-target", "-t", help="Path where the generated file will be stored.")
def main(target: str):
    creation_info = CreationInfo(spdx_version="SPDX-2.3", spdx_id="SPDXRef-DOCUMENT", name="document name",
                                 data_license="CC0-1.0", document_namespace="https://some.namespace",
                                 creators=[Actor(ActorType.TOOL, "test-tool")],
                                 created=datetime(2022, 1, 1))
    annotation = Annotation(spdx_id="SPDXRef-somepackage", annotation_type=AnnotationType.OTHER,
                            annotation_date=datetime(2022, 1, 1), annotation_comment="Package level annotation",
                            annotator=Actor(ActorType.PERSON, "Package Annotator"))
    package = Package(name="package name", spdx_id="SPDXRef-somepackage", version="2.2.1", file_name="./foo.bar",
                      supplier=Actor(ActorType.PERSON, "Jane Doe", "jane.doe@example.com"),
                      originator=Actor(ActorType.ORGANIZATION, "some organization", "contact@example.com"),
                      download_location="http://download.com", files_analyzed=True,
                      verification_code=PackageVerificationCode(value="d6a770ba38583ed4bb4525bd96e50461655d2758",
                                                                excluded_files=["./some.file"]),
                      checksums=[Checksum(ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758"),
                                 Checksum(ChecksumAlgorithm.MD5, "624c1abb3664f4b35547e7c73864ad24")],
                      homepage="http://home.page", source_info="source information",
                      license_concluded=get_spdx_licensing().parse("GPL-2.0-only"),
                      license_info_from_files=[get_spdx_licensing().parse("GPL-2.0-only")],
                      license_declared=get_spdx_licensing().parse("GPL-2.0-only"), license_comment="license comment",
                      copyright_text="Copyright 2022 Jane Doe", summary="package summary",
                      description="package description", comment="package comment",
                      attribution_texts=["package attribution"], primary_package_purpose=PackagePurpose.LIBRARY,
                      release_date=datetime(2015, 1, 1), built_date=datetime(2014, 1, 1),
                      valid_until_date=datetime(2022, 1, 1),
                      external_references=[ExternalPackageRef(category=ExternalPackageRefCategory.OTHER,
                                                              reference_type="http://reference.type",
                                                              locator="reference/locator",
                                                              comment="external reference comment")])
    file = File(spdx_id="SPDXRef-somefile", name="./foo.txt",
                checksums=[Checksum(ChecksumAlgorithm.SHA1, value="d6a770ba38583ed4bb4525bd96e50461655d2758")])
    relationships = [Relationship(spdx_element_id="SPDXRef-DOCUMENT", related_spdx_element_id="SPDXRef-somepackage",
                                  relationship_type=RelationshipType.DESCRIBES),
                     Relationship(spdx_element_id="SPDXRef-somepackage", relationship_type=RelationshipType.CONTAINS,
                                  related_spdx_element_id="SPDXRef-somefile")]

    doc = Document(creation_info, packages=[package], files=[file], relationships=relationships,
                   annotations=[annotation])
    write_document_to_file(doc, target)


if __name__ == "__main__":
    main()
